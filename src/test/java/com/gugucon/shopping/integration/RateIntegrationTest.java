package com.gugucon.shopping.integration;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.item.repository.RateStatRepository;
import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.member.domain.vo.Gender;
import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.order.domain.PayType;
import com.gugucon.shopping.order.dto.request.OrderPayRequest;
import com.gugucon.shopping.pay.dto.request.PointPayRequest;
import com.gugucon.shopping.rate.dto.request.RateCreateRequest;
import com.gugucon.shopping.rate.dto.response.RateDetailResponse;
import com.gugucon.shopping.rate.dto.response.RateResponse;
import com.gugucon.shopping.utils.DomainUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static com.gugucon.shopping.member.domain.vo.BirthYearRange.*;
import static com.gugucon.shopping.utils.ApiUtils.*;
import static com.gugucon.shopping.utils.StatsUtils.createInitialRateStat;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("별점 기능 통합 테스트")
class RateIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RateStatRepository rateStatRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    @DisplayName("주문 상품에 별점을 남긴다")
    void rate() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final Long orderId = buyProductWithSuccess(restTemplate, accessToken, insertProduct("good product"));
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();
        final short score = 3;

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(new RateCreateRequest(orderItemId, score))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/rate")
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("존재하지 않는 주문 상품에 별점을 남기면 404 상태를 반환한다")
    void rate_notExistOrderItem_status404() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final long notExistOrderItemId = 1_000_000_000L;
        final short score = 3;

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(new RateCreateRequest(notExistOrderItemId, score))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/rate")
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_ITEM);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.INVALID_ORDER_ITEM.getMessage());
    }

    @Test
    @DisplayName("사용자가 주문하지 않은 주문 상품에 별점을 남기면 404 상태를 반환한다")
    void rate_otherUsersOrderItem_status404() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");

        final String othersAccessToken = loginAfterSignUp("other_email@woowafriends.com", "test_password");
        final Long orderId = buyProductWithSuccess(restTemplate, othersAccessToken, insertProduct("good product"));
        final long othersOrderItemId = getFirstOrderItem(othersAccessToken, orderId).getId();

        final short score = 3;

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(new RateCreateRequest(othersOrderItemId, score))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/rate")
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_ITEM);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.INVALID_ORDER_ITEM.getMessage());
    }

    @Test
    @DisplayName("이미 별점을 남긴 주문 상품에 다시 별점을 남기면 400 상태를 반환한다")
    void rate_alreadyRated_status400() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");

        final Long orderId = buyProductWithSuccess(restTemplate, accessToken, insertProduct("good product"));
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();
        final short score = 3;

        createRateToOrderedItem(accessToken, new RateCreateRequest(orderItemId, score));

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(new RateCreateRequest(orderItemId, score))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/rate")
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.ALREADY_RATED);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.ALREADY_RATED.getMessage());
    }

    @ParameterizedTest
    @DisplayName("별점이 1-5 사이의 정수가 아니면 400 상태를 반환한다")
    @ValueSource(shorts = {0, 6})
    void rate_notInvalidScore_status400(final short score) {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final Long orderId = buyProductWithSuccess(restTemplate, accessToken, insertProduct("good product"));
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(new RateCreateRequest(orderItemId, score))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/rate")
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_SCORE);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.INVALID_SCORE.getMessage());
    }

    @Test
    @DisplayName("결제가 완료되지 않은 상품에 별점 생성을 시도하면 404 상태를 반환한다")
    void rate_notPayedOrderItem_status404() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");

        final Long productId = insertProduct("testProduct");
        insertCartItem(accessToken, new CartItemInsertRequest(productId));
        final Long orderId = placeOrder(accessToken);
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();

        final short score = 3;

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(new RateCreateRequest(orderItemId, score))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/rate")
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_ITEM);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.INVALID_ORDER_ITEM.getMessage());
    }

    @Test
    @DisplayName("상품의 평균 별점 정보를 가져온다")
    void getAverageRate() {
        // given
        final Long productId = insertProduct("good Product");
        initializeAllAgeAndGenderProductStats(productId);

        final int rateCount = 5;
        final double averageRate = createRateToProduct(productId, rateCount);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/api/v1/rate/product/{productId}", productId)
                .then()
                .contentType(ContentType.JSON)
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final RateResponse rateResponse = response.as(RateResponse.class);
        
        assertThat(rateResponse.getRateCount()).isEqualTo(rateCount);
        assertThat(rateResponse.getAverageRate()).isEqualTo(Math.floor(averageRate * 100) / 100.0);
    }

    @Test
    @DisplayName("상품의 평균 별점 조회 시, 상품이 존재하지 않으면 404 상태를 반환한다")
    void getAverageRate_notExistOrderItemId_status404() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final long notExistProductId = 1_000_000_000L;

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .get("/api/v1/rate/product/{productId}", notExistProductId)
                .then()
                .contentType(ContentType.JSON)
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PRODUCT);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.INVALID_PRODUCT.getMessage());
    }

    @Test
    @DisplayName("사용자가 주문 상품에 남긴 별점 정보를 가져온다")
    void getRateDetail() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final Long orderId = buyProductWithSuccess(restTemplate, accessToken, insertProduct("good product"));
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();
        final short score = 3;
        createRateToOrderedItem(accessToken, new RateCreateRequest(orderItemId, score));

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .get("/api/v1/rate/orderItem/{orderItemId}", orderItemId)
                .then()
                .contentType(ContentType.JSON)
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final RateDetailResponse rateDetailResponse = response.as(RateDetailResponse.class);
        assertThat(rateDetailResponse.getScore()).isEqualTo(score);
    }

    @Test
    @DisplayName("사용자가 주문 상품에 남긴 별점 정보 조회 시, 별점 정보가 존재하지 않으면 404 상태를 반환한다")
    void getRateDetail_notExistRate_status404() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final Long orderId = buyProductWithSuccess(restTemplate, accessToken, insertProduct("good product"));
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .get("/api/v1/rate/orderItem/{orderItemId}", orderItemId)
                .then()
                .contentType(ContentType.JSON)
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_RATE);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.INVALID_RATE.getMessage());
    }

    @Test
    @DisplayName("해당 물품의 주문자들 중 현재 접속한 사용자와 같은 나이대와 성별을 가진 주문자들의 평균 별점 정보를 가져온다")
    void getCustomRate1() {
        // given
        int sequence = 0; // for Unique Email

        final Gender gender = Gender.MALE;
        final int age = 24;
        final LocalDate birthDate = LocalDate.of(LocalDate.now().getYear() - age + 1, 1, 1);
        final String accessToken = loginAfterSignUp(createSignupRequest(sequence++, gender, birthDate.getYear()));

        final String 십대_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(UNDER_TEENS)));
        final String 십대_남자_1_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.MALE, getYear(UNDER_TEENS)));

        final String 이십대_초반_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(EARLY_TWENTIES)));
        final String 이십대_초반_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(EARLY_TWENTIES)));

        final String 이십대_중반_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(MID_TWENTIES)));
        final String 이십대_중반_남자_2_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(MID_TWENTIES)));
        final String 이십대_중반_남자_3_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(MID_TWENTIES)));
        final String 이십대_중반_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(MID_TWENTIES)));

        final String 이십대_후반_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(LATE_TWENTIES)));
        final String 이십대_후반_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(LATE_TWENTIES)));

        final String 삼십대_남자_1_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.MALE, getYear(THIRTIES)));
        final String 삼십대_여자_1_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.FEMALE, getYear(THIRTIES)));

        final String 사십대_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(OVER_FORTIES)));
        final String 사십대_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(OVER_FORTIES)));

        final Long productId = insertProduct("product");
        initializeAllAgeAndGenderProductStats(productId);

        rate(십대_여자_1_토큰, productId, 2);
        rate(십대_남자_1_토큰, productId, 1);
        rate(이십대_초반_여자_1_토큰, productId, 4);
        rate(이십대_초반_남자_1_토큰, productId, 4);
        rate(이십대_중반_남자_1_토큰, productId, 1);
        rate(이십대_중반_남자_2_토큰, productId, 5);
        rate(이십대_중반_남자_3_토큰, productId, 3);
        rate(이십대_중반_여자_1_토큰, productId, 1);
        rate(이십대_후반_여자_1_토큰, productId, 5);
        rate(이십대_후반_남자_1_토큰, productId, 2);
        rate(삼십대_남자_1_토큰, productId, 2);
        rate(삼십대_여자_1_토큰, productId, 4);
        rate(사십대_여자_1_토큰, productId, 1);
        rate(사십대_남자_1_토큰, productId, 2);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/v1/rate/product/{productId}/custom", productId)
                .then()
                .contentType(ContentType.JSON)
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final RateResponse rateResponse = response.as(RateResponse.class);
        assertThat(rateResponse.getRateCount()).isEqualTo(3);
        assertThat(rateResponse.getAverageRate()).isCloseTo(3.0, Percentage.withPercentage(99.9));
    }

    @Test
    @DisplayName("해당 물품의 주문자들 중 현재 접속한 사용자와 같은 나이대와 성별을 가진 주문자들의 평균 별점 정보를 가져온다")
    void getCustomRate2() {
        // given
        int sequence = 0; // for Unique Email

        final Gender gender = Gender.MALE;
        final int age = 35;
        final LocalDate birthDate = LocalDate.of(LocalDate.now().getYear() - age + 1, 1, 1);
        final String accessToken = loginAfterSignUp(createSignupRequest(sequence++, gender, birthDate.getYear()));

        final String 십대_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(UNDER_TEENS)));
        final String 십대_남자_1_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.MALE, getYear(UNDER_TEENS)));

        final String 이십대_초반_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(EARLY_TWENTIES)));
        final String 이십대_초반_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(EARLY_TWENTIES)));

        final String 이십대_중반_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(MID_TWENTIES)));
        final String 이십대_중반_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(MID_TWENTIES)));

        final String 이십대_후반_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(LATE_TWENTIES)));
        final String 이십대_후반_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(LATE_TWENTIES)));

        final String 삼십대_남자_1_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.MALE, getYear(THIRTIES)));
        final String 삼십대_남자_2_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.MALE, getYear(THIRTIES)));
        final String 삼십대_남자_3_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.MALE, getYear(THIRTIES)));
        final String 삼십대_여자_1_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.FEMALE, getYear(THIRTIES)));

        final String 사십대_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(OVER_FORTIES)));
        final String 사십대_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(OVER_FORTIES)));

        final Long productId = insertProduct("product");

        initializeAllAgeAndGenderProductStats(productId);
        rate(십대_여자_1_토큰, productId, 2);
        rate(십대_남자_1_토큰, productId, 1);
        rate(이십대_초반_여자_1_토큰, productId, 4);
        rate(이십대_초반_남자_1_토큰, productId, 4);
        rate(이십대_중반_남자_1_토큰, productId, 1);
        rate(이십대_중반_여자_1_토큰, productId, 1);
        rate(이십대_후반_여자_1_토큰, productId, 5);
        rate(이십대_후반_남자_1_토큰, productId, 2);
        rate(삼십대_남자_1_토큰, productId, 2);
        rate(삼십대_남자_2_토큰, productId, 2);
        rate(삼십대_남자_3_토큰, productId, 2);
        rate(삼십대_여자_1_토큰, productId, 4);
        rate(사십대_여자_1_토큰, productId, 1);
        rate(사십대_남자_1_토큰, productId, 2);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/v1/rate/product/{productId}/custom", productId)
                .then()
                .contentType(ContentType.JSON)
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final RateResponse rateResponse = response.as(RateResponse.class);
        assertThat(rateResponse.getRateCount()).isEqualTo(3);
        assertThat(rateResponse.getAverageRate()).isCloseTo(2.0, Percentage.withPercentage(99.9));
    }

    @Test
    @DisplayName("해당 물품의 주문자들 중 현재 접속한 사용자와 같은 나이대와 성별을 가진 주문자들의 평균 별점 정보를 가져온다")
    void getCustomRate3() {
        // given
        int sequence = 0; // for Unique Email

        final Gender gender = Gender.FEMALE;
        final int age = 19;
        final LocalDate birthDate = LocalDate.of(LocalDate.now().getYear() - age + 1, 1, 1);
        final String accessToken = loginAfterSignUp(createSignupRequest(sequence++, gender, birthDate.getYear()));

        final String 십대_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(UNDER_TEENS)));
        final String 십대_여자_2_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(UNDER_TEENS)));
        final String 십대_여자_3_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(UNDER_TEENS)));
        final String 십대_남자_1_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.MALE, getYear(UNDER_TEENS)));

        final String 이십대_초반_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(EARLY_TWENTIES)));
        final String 이십대_초반_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(EARLY_TWENTIES)));

        final String 이십대_중반_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(MID_TWENTIES)));
        final String 이십대_중반_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(MID_TWENTIES)));

        final String 이십대_후반_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(LATE_TWENTIES)));
        final String 이십대_후반_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(LATE_TWENTIES)));

        final String 삼십대_남자_1_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.MALE, getYear(THIRTIES)));
        final String 삼십대_여자_1_토큰 = loginAfterSignUp(createSignupRequest(sequence++, Gender.FEMALE, getYear(THIRTIES)));

        final String 사십대_여자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.FEMALE, getYear(OVER_FORTIES)));
        final String 사십대_남자_1_토큰 = loginAfterSignUp(
                createSignupRequest(sequence++, Gender.MALE, getYear(OVER_FORTIES)));

        final Long productId = insertProduct("product");

        initializeAllAgeAndGenderProductStats(productId);
        rate(십대_여자_1_토큰, productId, 2);
        rate(십대_여자_2_토큰, productId, 5);
        rate(십대_여자_3_토큰, productId, 2);
        rate(십대_남자_1_토큰, productId, 1);
        rate(이십대_초반_여자_1_토큰, productId, 4);
        rate(이십대_초반_남자_1_토큰, productId, 4);
        rate(이십대_중반_남자_1_토큰, productId, 1);
        rate(이십대_중반_여자_1_토큰, productId, 1);
        rate(이십대_후반_여자_1_토큰, productId, 5);
        rate(이십대_후반_남자_1_토큰, productId, 2);
        rate(삼십대_남자_1_토큰, productId, 2);
        rate(삼십대_여자_1_토큰, productId, 4);
        rate(사십대_여자_1_토큰, productId, 1);
        rate(사십대_남자_1_토큰, productId, 2);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/v1/rate/product/{productId}/custom", productId)
                .then()
                .contentType(ContentType.JSON)
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final RateResponse rateResponse = response.as(RateResponse.class);
        assertThat(rateResponse.getRateCount()).isEqualTo(3);
        assertThat(rateResponse.getAverageRate()).isCloseTo(3.0, Percentage.withPercentage(99.9));
    }

    private int getYear(final BirthYearRange birthYearRange) {
        return birthYearRange.getStartDate().getYear();
    }

    private void rate(final String accessToken, final Long productId, final int score) {
        insertCartItem(accessToken, new CartItemInsertRequest(productId));
        chargePoint(accessToken, 1000000L);
        final Long orderId = placeOrder(accessToken);
        putOrder(accessToken, new OrderPayRequest(orderId, PayType.POINT));
        payOrderByPoint(accessToken, new PointPayRequest(orderId));
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();
        createRateToOrderedItem(accessToken, new RateCreateRequest(orderItemId, (short) score));
    }

    private SignupRequest createSignupRequest(final int sequence,
                                              final Gender gender,
                                              final int birthYear) {
        return SignupRequest.builder()
                .email("email@".concat(String.valueOf(sequence)).concat(".com"))
                .password("password").passwordCheck("password")
                .nickname("nickname")
                .gender(gender.toString())
                .birthDate(LocalDate.of(birthYear, 1, 2))
                .build();
    }

    private Long insertProduct(final String productName) {
        final Product product = DomainUtils.createProductWithoutId(productName, 1000, 100);
        productRepository.save(product);
        return product.getId();
    }

    private void createProductStats(final Gender gender, final BirthYearRange birthYearRange, final long productId) {
        rateStatRepository.save(createInitialRateStat(gender, birthYearRange, productId));
    }

    private void initializeAllAgeAndGenderProductStats(final long productId) {
        createProductStats(Gender.FEMALE, UNDER_TEENS, productId);
        createProductStats(Gender.MALE, UNDER_TEENS, productId);
        createProductStats(Gender.FEMALE, EARLY_TWENTIES, productId);
        createProductStats(Gender.MALE, EARLY_TWENTIES, productId);
        createProductStats(Gender.FEMALE, MID_TWENTIES, productId);
        createProductStats(Gender.MALE, MID_TWENTIES, productId);
        createProductStats(Gender.FEMALE, LATE_TWENTIES, productId);
        createProductStats(Gender.MALE, LATE_TWENTIES, productId);
        createProductStats(Gender.FEMALE, THIRTIES, productId);
        createProductStats(Gender.MALE, THIRTIES, productId);
        createProductStats(Gender.FEMALE, OVER_FORTIES, productId);
        createProductStats(Gender.MALE, OVER_FORTIES, productId);
    }

    private double createRateToProduct(final Long productId, final int count) {
        mockServerSuccess(restTemplate, count);
        double totalScore = 0;
        for (int i = 0; i < count; i++) {
            final String accessToken = loginAfterSignUp("test_email" + i + "@woowafriends.com", "test_password!");
            final Long orderId = buyProduct(accessToken, productId, 5);
            final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();
            final short score = (short) (Math.random() * 5 + 1);
            totalScore += score;
            createRateToOrderedItem(accessToken, new RateCreateRequest(orderItemId, score));
        }
        return totalScore / count;
    }
}
