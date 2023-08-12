package com.gugucon.shopping;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;

import java.util.List;

public class TestUtils {

    private static Long sequence = 0L;

    public static Product createProduct(String name, long price) {
        sequence++;
        return new Product(sequence, name, "image_file_name_" + sequence, 100, "test_description", price);
    }

    public static Member createMember() {
        sequence++;
        return new Member(sequence, "test_email" + sequence + "@gmail.com", "test_password");
    }

    public static ExtractableResponse<Response> login(final LoginRequest loginRequest) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login/token")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> insertCartItem(String accessToken,
                                                               CartItemInsertRequest cartItemInsertRequest) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(cartItemInsertRequest)
                .when().post("/api/v1/cart/items")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> readCartItems(String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/v1/cart/items")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> placeOrder(String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .post("/api/v1/order")
                .then()
                .extract();
    }

    public static Long extractOrderId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    public static List<CartItemResponse> extractCartItemResponses(
            final ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", CartItemResponse.class);
    }
}
