package com.gugucon.shopping.integration;

import static com.gugucon.shopping.utils.ApiUtils.getFirstOrderItem;
import static com.gugucon.shopping.utils.ApiUtils.insertCartItem;
import static com.gugucon.shopping.utils.ApiUtils.loginAfterSignUp;
import static com.gugucon.shopping.utils.ApiUtils.placeOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.RateCreateRequest;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.utils.ApiUtils;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
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
        final Long orderId = buyProductWithSuccess(accessToken, "good product");
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
        final Long orderId = buyProductWithSuccess(othersAccessToken, "good product");
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

        final Long orderId = buyProductWithSuccess(accessToken, "good product");
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();
        final short score = 3;
        createRateToOrderedItem(accessToken, orderItemId, score);

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
        final Long orderId = buyProductWithSuccess(accessToken, "good product");
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
    @DisplayName("결제가 완료되지 않은 상품에 별점 생성을 시도하면 400 상태를 반환한다")
    void rate_notPayedOrderItem_status400() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final Long productId = insertProduct("testProduct", 1000L);
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NOT_PAYED_ORDER);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.NOT_PAYED_ORDER.getMessage());
    }

    @Test
    @DisplayName("상품의 평균 별점 정보를 가져온다")
    void getAverageRate() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final Long orderId = buyProductWithSuccess(accessToken, "good product");
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();
        final short score = 3;
        createRateToOrderedItem(accessToken, orderItemId, score);

        // when

        // then
    }

    @Test
    @DisplayName("상품의 평균 별점 조회 시, 상품이 존재하지 않으면 404 상태를 반환한다")
    void getAverageRate_notExistOrderItemId_status404() {
        // given

        // when

        // then
    }

    private Long insertProduct(final String productName, final long price) {
        final Product product = DomainUtils.createProductWithoutId(productName, price, 10);
        productRepository.save(product);
        return product.getId();
    }

    private Long buyProductWithSuccess(String accessToken, String productName) {
        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(ExpectedCount.twice(), anything())
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{ \"status\": \"DONE\" }", MediaType.APPLICATION_JSON));
        return ApiUtils.buyProduct(accessToken, insertProduct(productName, 1000L), 10);
    }

    private void createRateToOrderedItem(final String accessToken, final Long orderItemId, final short score) {
        RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .body(new RateCreateRequest(orderItemId, score))
            .contentType(ContentType.JSON)
            .when()
            .post("/api/v1/rate")
            .then()
            .extract();
    }
}
