package com.gugucon.shopping.utils;

import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.member.dto.response.LoginResponse;
import com.gugucon.shopping.order.dto.request.OrderPayRequest;
import com.gugucon.shopping.order.dto.response.OrderDetailResponse;
import com.gugucon.shopping.order.dto.response.OrderHistoryResponse;
import com.gugucon.shopping.order.dto.response.OrderItemResponse;
import com.gugucon.shopping.order.dto.response.OrderPayResponse;
import com.gugucon.shopping.pay.dto.request.TossPayRequest;
import com.gugucon.shopping.pay.dto.response.PayResponse;
import com.gugucon.shopping.pay.dto.response.TossPayInfoResponse;
import com.gugucon.shopping.point.dto.request.PointChargeRequest;
import com.gugucon.shopping.rate.dto.request.RateCreateRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ApiUtils {

    public static String login(final LoginRequest loginRequest) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(loginRequest)
            .when().post("/api/v1/login")
            .then().log().all()
            .extract()
            .as(LoginResponse.class)
            .getAccessToken();
    }

    public static void signup(final SignupRequest signupRequest) {
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(signupRequest)
            .when().post("/api/v1/signup")
            .then().log().all();
    }

    public static void signUp(final String email, final String password) {
        final SignupRequest request = SignupRequest.builder()
                .email(email)
                .password(password).passwordCheck(password)
                .nickname("nickname")
                .gender("FEMALE")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();
        ApiUtils.signup(request);
    }

    public static String loginAfterSignUp(final String email, final String password) {
        signUp(email, password);
        final LoginRequest request = new LoginRequest(email, password);
        return ApiUtils.login(request);
    }

    public static ExtractableResponse<Response> insertCartItem(final String accessToken,
                                                               final CartItemInsertRequest cartItemInsertRequest) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemInsertRequest)
            .when().post("/api/v1/cart/items")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> updateCartItem(final String accessToken,
                                                               final long cartItemId,
                                                               final CartItemUpdateRequest cartItemUpdateRequest) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemUpdateRequest)
            .when().patch("/api/v1/cart/items/{cartItemId}", cartItemId)
            .then().log().all()
            .extract();
    }

    public static List<CartItemResponse> readCartItems(final String accessToken) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().get("/api/v1/cart/items")
            .then().log().all()
            .extract()
            .jsonPath()
            .getList(".", CartItemResponse.class);
    }

    public static Long placeOrder(final String accessToken) {
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when()
            .post("/api/v1/order")
            .then().log().all()
            .extract();
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    public static List<OrderHistoryResponse> getOrderHistory(final String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .get("/api/v1/order-history")
                .then().log().all()
                .extract()
                .jsonPath().getList(".", OrderHistoryResponse.class);
    }

    public static OrderDetailResponse getOrderDetail(final String accessToken, final Long orderId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .get("/api/v1/order/{orderId}", orderId)
                .then().log().all()
                .extract()
                .as(OrderDetailResponse.class);
    }

    public static OrderItemResponse getFirstOrderItem(final String accessToken, final Long orderId) {
        return getOrderDetail(accessToken, orderId).getOrderItems().get(0);
    }

    public static TossPayInfoResponse getPaymentInfo(final String accessToken, final Long orderId) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when()
            .get("/api/v1/pay/toss?orderId=" + orderId)
            .then().log().all()
            .extract()
            .as(TossPayInfoResponse.class);
    }

    public static OrderPayResponse putOrder(final String accessToken, final OrderPayRequest orderPayRequest) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(orderPayRequest)
                .when()
                .put("/api/v1/order")
                .then()
                .extract()
                .as(OrderPayResponse.class);
    }

    public static Long validatePayment(final String accessToken, final TossPayRequest tossPayRequest) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(tossPayRequest)
            .when()
            .post("/api/v1/pay/toss")
            .then().log().all()
            .extract()
            .as(PayResponse.class)
            .getOrderId();
    }

    public static Long buyProduct(final String accessToken, final Long productId, final int quantity) {
        insertCartItem(accessToken, new CartItemInsertRequest(productId));
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
        updateCartItem(accessToken, cartItemResponses.get(0).getCartItemId(), new CartItemUpdateRequest(quantity));
        final Long orderId = placeOrder(accessToken);
        putOrder(accessToken, new OrderPayRequest(orderId, "TOSS"));
        final TossPayInfoResponse tossPayInfoResponse = getPaymentInfo(accessToken, orderId);
        final TossPayRequest tossPayRequest = new TossPayRequest("mockPaymentKey",
                                                                 tossPayInfoResponse.getEncodedOrderId(),
                                                                 tossPayInfoResponse.getPrice(),
                                                                 "mockPaymentType");
        return validatePayment(accessToken, tossPayRequest);
    }

    public static void chargePoint(String accessToken, Long chargePoint) {
        RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new PointChargeRequest(chargePoint))
                .when()
                .put("/api/v1/point")
                .then().log().all();
    }

    public static void createRateToOrderedItem(final String accessToken, final RateCreateRequest request) {
        RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .body(request)
            .contentType(ContentType.JSON)
            .when()
            .post("/api/v1/rate")
            .then()
            .extract();
    }

    public static Long buyProductWithSuccess(final RestTemplate restTemplate,
                                             final String accessToken,
                                             final Long productId) {
        mockServerSuccess(restTemplate, 1);
        return ApiUtils.buyProduct(accessToken, productId, 10);
    }

    public static void mockServerSuccess(final RestTemplate restTemplate, final int count) {
        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(ExpectedCount.times(count), anything())
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{ \"status\": \"DONE\" }", MediaType.APPLICATION_JSON));
    }
}
