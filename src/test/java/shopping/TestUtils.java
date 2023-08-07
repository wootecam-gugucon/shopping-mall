package shopping;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import shopping.auth.dto.request.LoginRequest;
import shopping.cart.domain.entity.Product;
import shopping.cart.dto.request.CartItemInsertRequest;

public class TestUtils {

    private static Long sequence = 0L;

    public static Product createProduct(String name, int price) {
        sequence++;
        return new Product(sequence, name, "image_file_name_" + sequence, price);
    }

    public static ExtractableResponse<Response> login(final LoginRequest loginRequest) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(loginRequest)
            .when().post("/login/token")
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
            .when().post("/cart/items")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> readCartItems(String accessToken) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().get("/cart/items")
            .then().log().all()
            .extract();
    }
}
