package com.gugucon.shopping.integration;

import static com.gugucon.shopping.utils.ApiUtils.buyProduct;
import static com.gugucon.shopping.utils.ApiUtils.createPayment;
import static com.gugucon.shopping.utils.ApiUtils.getPaymentInfo;
import static com.gugucon.shopping.utils.ApiUtils.insertCartItem;
import static com.gugucon.shopping.utils.ApiUtils.placeOrder;
import static com.gugucon.shopping.utils.ApiUtils.validatePayment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.order.repository.OrderItemRepository;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.pay.dto.request.PayCreateRequest;
import com.gugucon.shopping.pay.dto.request.PayValidationRequest;
import com.gugucon.shopping.pay.dto.response.PayCreateResponse;
import com.gugucon.shopping.pay.dto.response.PayInfoResponse;
import com.gugucon.shopping.pay.dto.response.PayValidationResponse;
import com.gugucon.shopping.pay.repository.PayRepository;
import com.gugucon.shopping.utils.ApiUtils;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
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

@IntegrationTest
@DisplayName("결제 기능 통합 테스트")
class PayIntegrationTest {

    @Value("${pay.callback.success-url}")
    private String successUrl;
    @Value("${pay.callback.fail-url}")
    private String failUrl;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PayRepository payRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @AfterEach
    void tearDown() {
        payRepository.deleteAll();
        orderRepository.deleteAll();
        orderItemRepository.deleteAll();
        cartItemRepository.deleteAll();
    }

    @Test
    @DisplayName("주문에 대한 결제 정보를 생성한다.")
    void createPayment_() {
        // given
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final Long orderId = placeOrder(accessToken);
        final PayCreateRequest payCreateRequest = new PayCreateRequest(orderId);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payCreateRequest)
                .when()
                .put("/api/v1/pay")
                .then().log().all()
                .extract();

        // then
        final PayCreateResponse payCreateResponse = response.as(PayCreateResponse.class);
        assertThat(payCreateResponse.getPayId()).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("결제 정보를 조회한다.")
    void getPaymentInfo_() {
        // given
        final String email = "test_email@woowafriends.com";
        final String accessToken = signUpAndLogin(email, "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final Long orderId = placeOrder(accessToken);
        final Long payId = createPayment(accessToken, new PayCreateRequest(orderId));

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .get("/api/v1/pay/{payId}", payId)
                .then().log().all()
                .extract();

        // then
        final PayInfoResponse payInfoResponse = response.as(PayInfoResponse.class);
        assertThat(payInfoResponse.getEncodedOrderId()).isNotEmpty();
        assertThat(payInfoResponse.getOrderName()).isEqualTo("치킨");
        assertThat(payInfoResponse.getPrice()).isEqualTo(20000);
        assertThat(payInfoResponse.getCustomerEmail()).isEqualTo(email);
        assertThat(payInfoResponse.getCustomerKey()).isNotEmpty();
        assertThat(payInfoResponse.getSuccessUrl()).isEqualTo(successUrl);
        assertThat(payInfoResponse.getFailUrl()).isEqualTo(failUrl);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("결제를 검증한다.")
    void validatePayment_() {
        // given
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final Long orderId = placeOrder(accessToken);
        final Long payId = createPayment(accessToken, new PayCreateRequest(orderId));
        final PayInfoResponse payInfoResponse = getPaymentInfo(accessToken, payId);
        final PayValidationRequest payValidationRequest = new PayValidationRequest("mockPaymentKey",
                                                                                   payInfoResponse.getEncodedOrderId(),
                                                                                   payInfoResponse.getPrice(),
                                                                                   "mockPaymentType");

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(ExpectedCount.once(), anything())
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{ \"status\": \"DONE\" }", MediaType.APPLICATION_JSON));

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payValidationRequest)
                .when()
                .post("/api/v1/pay/validate")
                .then().log().all()
                .extract();

        // then
        final PayValidationResponse payValidationResponse = response.as(PayValidationResponse.class);
        assertThat(payValidationResponse.getOrderId()).isEqualTo(orderId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("외부 API 검증 요청에 실패하면 결제 검증을 요청했을 때 500 상태코드를 반환한다.")
    void validatePaymentFail_externalValidationFail() {
        // given
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final Long orderId = placeOrder(accessToken);
        final Long payId = createPayment(accessToken, new PayCreateRequest(orderId));
        final PayInfoResponse payInfoResponse = getPaymentInfo(accessToken, payId);
        final PayValidationRequest payValidationRequest = new PayValidationRequest("mockPaymentKey",
                                                                                   payInfoResponse.getEncodedOrderId(),
                                                                                   payInfoResponse.getPrice(),
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
                .body(payValidationRequest)
                .when()
                .post("/api/v1/pay/validate")
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.UNKNOWN_ERROR);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("재고가 부족하면 결제 검증을 요청했을 때 400 상태코드를 반환한다.")
    void validatePaymentFail_stockNotEnough() {
        // given
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(3L));
        final Long orderId = placeOrder(accessToken);
        final Long payId = createPayment(accessToken, new PayCreateRequest(orderId));
        final PayInfoResponse payInfoResponse = getPaymentInfo(accessToken, payId);
        final PayValidationRequest payValidationRequest = new PayValidationRequest("mockPaymentKey",
                                                                                   payInfoResponse.getEncodedOrderId(),
                                                                                   payInfoResponse.getPrice(),
                                                                                   "mockPaymentType");

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(ExpectedCount.twice(), anything())
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{ \"status\": \"DONE\" }", MediaType.APPLICATION_JSON));

        final String otherAccessToken = signUpAndLogin("other_test_email@woowafriends.com", "test_password!");
        buyProduct(otherAccessToken, 3L, 100);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payValidationRequest)
                .when()
                .post("/api/v1/pay/validate")
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.STOCK_NOT_ENOUGH);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("이미 결제가 완료되었으면 결제 검증을 요청했을 때 400 상태코드를 반환한다.")
    void validatePaymentFail_payedOrder() {
        // given
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final Long orderId = placeOrder(accessToken);
        final Long payId = createPayment(accessToken, new PayCreateRequest(orderId));
        final PayInfoResponse payInfoResponse = getPaymentInfo(accessToken, payId);
        final PayValidationRequest payValidationRequest = new PayValidationRequest("mockPaymentKey",
                                                                                   payInfoResponse.getEncodedOrderId(),
                                                                                   payInfoResponse.getPrice(),
                                                                                   "mockPaymentType");

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(ExpectedCount.twice(), anything())
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{ \"status\": \"DONE\" }", MediaType.APPLICATION_JSON));

        validatePayment(accessToken, payValidationRequest);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payValidationRequest)
                .when()
                .post("/api/v1/pay/validate")
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.PAYED_ORDER);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("다른 회원의 주문에 대해 결제 검증을 요청했을 때 400 상태코드를 반환한다.")
    void validatePaymentFail_orderOfOtherMember() {
        // given
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final Long orderId = placeOrder(accessToken);
        final Long payId = createPayment(accessToken, new PayCreateRequest(orderId));
        final PayInfoResponse payInfoResponse = getPaymentInfo(accessToken, payId);
        final PayValidationRequest payValidationRequest = new PayValidationRequest("mockPaymentKey",
                                                                                   payInfoResponse.getEncodedOrderId(),
                                                                                   payInfoResponse.getPrice(),
                                                                                   "mockPaymentType");

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(ExpectedCount.twice(), anything())
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{ \"status\": \"DONE\" }", MediaType.APPLICATION_JSON));

        final String otherAccessToken = signUpAndLogin("other_test_email@woowafriends.com", "test_password!");

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(otherAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payValidationRequest)
                .when()
                .post("/api/v1/pay/validate")
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void signUp(final String email, final String password) {
        final SignupRequest request = new SignupRequest(email, password, password,"testUser");
        ApiUtils.signup(request);
    }

    private String signUpAndLogin(final String email, final String password) {
        signUp(email, password);
        final LoginRequest request = new LoginRequest(email, password);
        return ApiUtils.login(request);
    }
}
