package com.gugucon.shopping.integration;

import static com.gugucon.shopping.utils.ApiUtils.buyProduct;
import static com.gugucon.shopping.utils.ApiUtils.buyProductWithSuccess;
import static com.gugucon.shopping.utils.ApiUtils.createRateToOrderedItem;
import static com.gugucon.shopping.utils.ApiUtils.getFirstOrderItem;
import static com.gugucon.shopping.utils.ApiUtils.insertCartItem;
import static com.gugucon.shopping.utils.ApiUtils.loginAfterSignUp;
import static com.gugucon.shopping.utils.ApiUtils.mockServerSuccess;
import static com.gugucon.shopping.utils.ApiUtils.placeOrder;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.RateCreateRequest;
import com.gugucon.shopping.item.dto.response.RateResponse;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.utils.DomainUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

@IntegrationTest
@DisplayName("별점 기능 통합 테스트")
class RateIntegrationTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

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
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_RATE);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.INVALID_RATE.getMessage());
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
        final double roundedAverageRate = Double.parseDouble(String.format("%.2f", averageRate));
        assertThat(rateResponse.getRateCount()).isEqualTo(rateCount);
        assertThat(rateResponse.getAverageRate()).isEqualTo(Double.valueOf(roundedAverageRate));
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

    private Long insertProduct(final String productName) {
        final Product product = DomainUtils.createProductWithoutId(productName, 1000, 100);
        productRepository.save(product);
        return product.getId();
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
