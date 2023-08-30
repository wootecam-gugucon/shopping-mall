package com.gugucon.shopping.integration;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.order.domain.PayType;
import com.gugucon.shopping.order.dto.request.OrderPayRequest;
import com.gugucon.shopping.pay.dto.request.TossPayFailRequest;
import com.gugucon.shopping.pay.dto.request.TossPayRequest;
import com.gugucon.shopping.pay.dto.response.PayResponse;
import com.gugucon.shopping.pay.dto.response.TossPayFailResponse;
import com.gugucon.shopping.pay.dto.response.TossPayInfoResponse;
import com.gugucon.shopping.utils.DomainUtils;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static com.gugucon.shopping.utils.ApiUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@IntegrationTest
@DisplayName("토스 결제 기능 통합 테스트")
class TossPayIntegrationTest {

    @Autowired
    ProductRepository productRepository;
    @Value("${pay.callback.success-url}")
    private String successUrl;
    @Value("${pay.callback.fail-url}")
    private String failUrl;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    @DisplayName("결제 정보를 조회한다.")
    void getPaymentInfo_() {
        // given
        final String email = "test_email@woowafriends.com";
        final String accessToken = loginAfterSignUp(email, "test_password!");

        addProductToCart(accessToken, "치킨");
        final Long orderId = placeOrder(accessToken);
        putOrder(accessToken, new OrderPayRequest(orderId, PayType.TOSS));

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .get("/api/v1/pay/toss?orderId=" + orderId)
                .then().log().all()
                .extract();

        // then
        final TossPayInfoResponse tossPayInfoResponse = response.as(TossPayInfoResponse.class);
        assertThat(tossPayInfoResponse.getEncodedOrderId()).isNotEmpty();
        assertThat(tossPayInfoResponse.getOrderName()).isEqualTo("치킨");
        assertThat(tossPayInfoResponse.getPrice()).isEqualTo(1000L);
        assertThat(tossPayInfoResponse.getCustomerKey()).isNotEmpty();
        assertThat(tossPayInfoResponse.getSuccessUrl()).isEqualTo(successUrl);
        assertThat(tossPayInfoResponse.getFailUrl()).isEqualTo(failUrl);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("결제를 검증한다.")
    void validatePayment_() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        addProductToCart(accessToken, "testProduct");

        final Long orderId = placeOrder(accessToken);
        putOrder(accessToken, new OrderPayRequest(orderId, PayType.TOSS));
        final TossPayInfoResponse tossPayInfoResponse = getPaymentInfo(accessToken, orderId);

        mockServerSuccess(restTemplate, 1);

        final TossPayRequest tossPayRequest = new TossPayRequest("mockPaymentKey",
                                                                 tossPayInfoResponse.getEncodedOrderId(),
                                                                 tossPayInfoResponse.getPrice(),
                                                                 "mockPaymentType");

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tossPayRequest)
                .when()
                .post("/api/v1/pay/toss")
                .then().log().all()
                .extract();

        // then
        final PayResponse payResponse = response.as(PayResponse.class);
        assertThat(payResponse.getOrderId()).isEqualTo(orderId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("외부 API 검증 요청에 실패하면 결제 검증을 요청했을 때 500 상태코드를 반환한다.")
    void validatePaymentFail_externalValidationFail() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        addProductToCart(accessToken, "testProduct");

        final Long orderId = placeOrder(accessToken);
        putOrder(accessToken, new OrderPayRequest(orderId, PayType.TOSS));
        final TossPayInfoResponse tossPayInfoResponse = getPaymentInfo(accessToken, orderId);
        final TossPayRequest tossPayRequest = new TossPayRequest("mockPaymentKey",
                                                                 tossPayInfoResponse.getEncodedOrderId(),
                                                                 tossPayInfoResponse.getPrice(),
                                                                 "mockPaymentType");

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(ExpectedCount.once(), anything())
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{ \"status\": \"ABORTED\" }", MediaType.APPLICATION_JSON));

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tossPayRequest)
                .when()
                .post("/api/v1/pay/toss")
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.UNKNOWN_ERROR);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("이미 결제가 완료되었으면 결제 검증을 요청했을 때 400 상태코드를 반환한다.")
    void validatePaymentFail_payedOrder() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        addProductToCart(accessToken, "testProduct");

        final Long orderId = placeOrder(accessToken);
        putOrder(accessToken, new OrderPayRequest(orderId, PayType.TOSS));
        final TossPayInfoResponse tossPayInfoResponse = getPaymentInfo(accessToken, orderId);
        final TossPayRequest tossPayRequest = new TossPayRequest("mockPaymentKey",
                                                                 tossPayInfoResponse.getEncodedOrderId(),
                                                                 tossPayInfoResponse.getPrice(),
                                                                 "mockPaymentType");

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(ExpectedCount.twice(), anything())
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{ \"status\": \"DONE\" }", MediaType.APPLICATION_JSON));

        validatePayment(accessToken, tossPayRequest);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tossPayRequest)
                .when()
                .post("/api/v1/pay/toss")
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_STATUS);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("다른 회원의 주문에 대해 결제 검증을 요청했을 때 404 상태코드를 반환한다.")
    void validatePaymentFail_orderOfOtherMember() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        addProductToCart(accessToken, "testProduct");

        final Long orderId = placeOrder(accessToken);
        putOrder(accessToken, new OrderPayRequest(orderId, PayType.TOSS));
        final TossPayInfoResponse tossPayInfoResponse = getPaymentInfo(accessToken, orderId);
        final TossPayRequest tossPayRequest = new TossPayRequest("mockPaymentKey",
                                                                 tossPayInfoResponse.getEncodedOrderId(),
                                                                 tossPayInfoResponse.getPrice(),
                                                                 "mockPaymentType");

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(ExpectedCount.twice(), anything())
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{ \"status\": \"DONE\" }", MediaType.APPLICATION_JSON));

        final String otherAccessToken = loginAfterSignUp("other_test_email@woowafriends.com", "test_password!");

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(otherAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tossPayRequest)
                .when()
                .post("/api/v1/pay/toss")
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("결제가 실패했을 때 orderId를 decode한 값을 반환한다.")
    void decodeOrderId() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        addProductToCart(accessToken, "testProduct");

        final Long orderId = placeOrder(accessToken);
        putOrder(accessToken, new OrderPayRequest(orderId, PayType.TOSS));
        final TossPayInfoResponse tossPayInfoResponse = getPaymentInfo(accessToken, orderId);
        final TossPayFailRequest tossPayFailRequest = new TossPayFailRequest("PAY_PROCESS_CANCELED",
                                                                             "사용자에 의해 결제가 취소되었습니다.",
                                                                             tossPayInfoResponse.getEncodedOrderId());

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tossPayFailRequest)
                .when()
                .post("/api/v1/pay/fail")
                .then().log().all()
                .extract();

        // then
        final TossPayFailResponse tossPayFailResponse = response.as(TossPayFailResponse.class);
        assertThat(tossPayFailResponse.getOrderId()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("결제가 실패했을 때 orderId를 decode할 수 없으면 500 상태코드를 반환한다.")
    void decodeOrderId_cannotDecode() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final TossPayFailRequest tossPayFailRequest = new TossPayFailRequest("PAY_PROCESS_CANCELED",
                                                                             "사용자에 의해 결제가 취소되었습니다.",
                                                                             "cannotDecodeOrderId");

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tossPayFailRequest)
                .when()
                .post("/api/v1/pay/fail")
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.UNKNOWN_ERROR);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void addProductToCart(final String accessToken,
                                  final String productName) {
        final Long productId = insertProduct(productName);
        insertCartItem(accessToken, new CartItemInsertRequest(productId));
    }

    private Long insertProduct(final String productName) {
        final Product product = DomainUtils.createProductWithoutId(productName, 1000L, 10);
        productRepository.save(product);
        return product.getId();
    }
}
