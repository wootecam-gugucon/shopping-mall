package com.gugucon.shopping;

import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.Password;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.member.dto.response.LoginResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class TestUtils {

    private static Long sequence = 0L;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static Product createProduct(String name, long price) {
        sequence++;
        return Product.builder()
                .id(sequence)
                .name(name)
                .imageFileName("image_file_name_" + sequence)
                .stock(100)
                .description("test_description")
                .price(price)
                .build();
    }

    public static Product createProduct(int stock) {
        sequence++;
        return Product.builder()
                .id(sequence)
                .name("name")
                .imageFileName("image_file_name_" + sequence)
                .stock(stock)
                .description("test_description")
                .price(1000L)
                .build();
    }

    public static Product createSoldOutProduct(String name, long price) {
        sequence++;
        return Product.builder()
                .id(sequence)
                .name(name)
                .imageFileName("image_file_name_" + sequence)
                .stock(0)
                .description("test_description")
                .price(price)
                .build();
    }

    public static Member createMember() {
        sequence++;
        return Member.builder()
                .id(sequence)
                .email("test_email" + sequence + "@gmail.com")
                .password(Password.of("test_password", passwordEncoder))
                .nickname("test_nickname_" + sequence)
                .build();
    }

    public static CartItem createCartItem() {
        sequence++;
        return CartItem.builder()
                .id(sequence)
                .memberId(1L)
                .product(createProduct(100))
                .quantity(1)
                .build();
    }

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

    public static List<CartItemResponse> readCartItems(String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/v1/cart/items")
                .then().log().all()
                .extract()
                .jsonPath()
                .getList(".", CartItemResponse.class);
    }

    public static Long placeOrder(String accessToken) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .post("/api/v1/order")
                .then()
                .extract();
        return Long.parseLong(response.header("Location").split("/")[2]);
    }
}
