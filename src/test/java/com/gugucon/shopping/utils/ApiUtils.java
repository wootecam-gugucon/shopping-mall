package com.gugucon.shopping.utils;

import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.member.dto.response.LoginResponse;
import com.gugucon.shopping.pay.dto.toss.request.TossPayCreateRequest;
import com.gugucon.shopping.pay.dto.toss.request.TossPayValidationRequest;
import com.gugucon.shopping.pay.dto.toss.response.TossPayCreateResponse;
import com.gugucon.shopping.pay.dto.toss.response.TossPayInfoResponse;
import com.gugucon.shopping.pay.dto.toss.response.TossPayValidationResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.http.MediaType;

public class ApiUtils {

    public static String login(final LoginRequest loginRequest) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(loginRequest)
            .when().post("/api/v1/login/token")
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
        final SignupRequest request = new SignupRequest(email, password, password,"testUser");
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
            .when().put("/api/v1/cart/items/{cartItemId}/quantity", cartItemId)
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

    public static Long createPayment(final String accessToken, final TossPayCreateRequest tossPayCreateRequest) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(tossPayCreateRequest)
            .when()
            .put("/api/v1/pay")
            .then().log().all()
            .extract()
            .as(TossPayCreateResponse.class)
            .getPayId();
    }

    public static TossPayInfoResponse getPaymentInfo(final String accessToken, final Long payId) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when()
            .get("/api/v1/pay/{payId}", payId)
            .then().log().all()
            .extract()
            .as(TossPayInfoResponse.class);
    }

    public static Long validatePayment(final String accessToken, final TossPayValidationRequest tossPayValidationRequest) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(tossPayValidationRequest)
            .when()
            .post("/api/v1/pay/validate")
            .then().log().all()
            .extract()
            .as(TossPayValidationResponse.class)
            .getOrderId();
    }

    public static Long buyProduct(final String accessToken, final Long productId, final int quantity) {
        insertCartItem(accessToken, new CartItemInsertRequest(productId));
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
        updateCartItem(accessToken, cartItemResponses.get(0).getCartItemId(), new CartItemUpdateRequest(quantity));
        final Long orderId = placeOrder(accessToken);
        final Long payId = createPayment(accessToken, new TossPayCreateRequest(orderId));
        final TossPayInfoResponse tossPayInfoResponse = getPaymentInfo(accessToken, payId);
        final TossPayValidationRequest tossPayValidationRequest = new TossPayValidationRequest("mockPaymentKey",
                                                                                               tossPayInfoResponse.getEncodedOrderId(),
                                                                                               tossPayInfoResponse.getPrice(),
                                                                                               "mockPaymentType");
        return validatePayment(accessToken, tossPayValidationRequest);
    }
}
