package shopping.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import shopping.TestUtils;
import shopping.auth.dto.request.LoginRequest;
import shopping.auth.dto.response.LoginResponse;
import shopping.cart.dto.request.CartItemInsertRequest;
import shopping.cart.dto.request.CartItemUpdateRequest;
import shopping.cart.dto.response.CartItemResponse;
import shopping.cart.repository.CartItemRepository;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ErrorResponse;

@DisplayName("장바구니 기능 통합 테스트")
class CartIntegrationTest extends IntegrationTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
    }

    @Test
    @DisplayName("성공 : 장바구니에 상품을 추가한다")
    void insertCartItem() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemInsertRequest)
            .when().post("/api/v1/cart/items")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("실패 : 장바구니에 이미 들어 있는 상품을 추가한다")
    void insertDuplicateCartItem() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        TestUtils.insertCartItem(accessToken, cartItemInsertRequest);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemInsertRequest)
            .when().post("/api/v1/cart/items")
            .then().log().all()
            .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_CART_ITEM);
    }

    @Test
    @DisplayName("실패 : productId 정보 없이 장바구니에 상품을 추가한다.")
    void insertCartItemWithoutProductId() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(null);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemInsertRequest)
            .when().post("/api/v1/cart/items")
            .then().log().all()
            .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD_MISSING);
    }

    @Test
    @DisplayName("실패 : 존재하지 않는 상품을 장바구니에 추가한다")
    void insertCartItemOfInvalidProduct() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final Long invalidProductId = Long.MAX_VALUE;
        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(
            invalidProductId);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemInsertRequest)
            .when().post("/api/v1/cart/items")
            .then().log().all()
            .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PRODUCT);
    }

    @Test
    @DisplayName("성공 : 장바구니 상품 목록을 조회한다")
    void readCartItems() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest1 = new CartItemInsertRequest(1L);
        final CartItemInsertRequest cartItemInsertRequest2 = new CartItemInsertRequest(2L);
        TestUtils.insertCartItem(accessToken, cartItemInsertRequest1);
        TestUtils.insertCartItem(accessToken, cartItemInsertRequest2);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().get("/api/v1/cart/items")
            .then().log().all()
            .extract();

        /* then */
        List<CartItemResponse> cartItemResponses = response.jsonPath()
            .getList(".", CartItemResponse.class);
        final List<String> names = cartItemResponses.stream()
            .map(CartItemResponse::getName)
            .collect(Collectors.toList());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(names).containsExactly("치킨", "피자");
        assertThat(cartItemResponses).hasSize(2);
    }

    @Test
    @DisplayName("성공 : 장바구니에 있는 하나의 상품 수량을 수정한다")
    void updateQuantityOfCartItem() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        TestUtils.insertCartItem(accessToken, cartItemInsertRequest);
        final List<CartItemResponse> cartItemResponses = TestUtils.readCartItems(accessToken)
            .jsonPath()
            .getList(".", CartItemResponse.class);

        final Long cartItemId = cartItemResponses.get(0).getCartItemId();
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(3);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemUpdateRequest)
            .when().put("/api/v1/cart/items/{cartItemId}/quantity", cartItemId)
            .then().log().all()
            .extract();

        /* then */
        final List<CartItemResponse> updatedCartItemResponses = TestUtils.readCartItems(accessToken)
            .jsonPath()
            .getList(".", CartItemResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(updatedCartItemResponses.get(0).getQuantity()).isEqualTo(
            cartItemUpdateRequest.getQuantity());
    }

    @Test
    @DisplayName("성공 : 장바구니에 있는 하나의 상품 수량을 0으로 수정하면 삭제된다.")
    void updateQuantityOfCartItemToZero() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        TestUtils.insertCartItem(accessToken, cartItemInsertRequest);
        final List<CartItemResponse> cartItemResponses = TestUtils.readCartItems(accessToken)
            .jsonPath()
            .getList(".", CartItemResponse.class);

        final Long cartItemId = cartItemResponses.get(0).getCartItemId();
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(0);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemUpdateRequest)
            .when().put("/api/v1/cart/items/{cartItemId}/quantity", cartItemId)
            .then().log().all()
            .extract();

        /* then */
        final List<CartItemResponse> updatedCartItemResponses = TestUtils.readCartItems(accessToken)
            .jsonPath()
            .getList(".", CartItemResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(updatedCartItemResponses).isEmpty();
    }

    @Test
    @DisplayName("실패 : 상품 수량을 0개 미만으로 수정한다.")
    void updateCartItemQuantityUnder0() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        TestUtils.insertCartItem(accessToken, cartItemInsertRequest);
        final List<CartItemResponse> cartItemResponses = TestUtils.readCartItems(accessToken)
            .jsonPath()
            .getList(".", CartItemResponse.class);

        final Long cartItemId = cartItemResponses.get(0).getCartItemId();
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(-1);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemUpdateRequest)
            .when().put("/api/v1/cart/items/{cartItemId}/quantity", cartItemId)
            .then().log().all()
            .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUANTITY);
    }

    @Test
    @DisplayName("실패 : 상품 수량을 1,000개 초과로 수정한다.")
    void updateCartItemQuantityOver1000() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        TestUtils.insertCartItem(accessToken, cartItemInsertRequest);
        final List<CartItemResponse> cartItemResponses = TestUtils.readCartItems(accessToken)
            .jsonPath()
            .getList(".", CartItemResponse.class);

        final Long cartItemId = cartItemResponses.get(0).getCartItemId();
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(1001);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemUpdateRequest)
            .when().put("/api/v1/cart/items/{cartItemId}/quantity", cartItemId)
            .then().log().all()
            .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUANTITY);
    }

    @Test
    @DisplayName("실패 : 다른 사용자의 장바구니 상품 수량을 수정한다.")
    void updateOtherUserCartItem() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        TestUtils.insertCartItem(accessToken, cartItemInsertRequest);
        final List<CartItemResponse> cartItemResponses = TestUtils.readCartItems(accessToken)
            .jsonPath()
            .getList(".", CartItemResponse.class);

        final Long cartItemId = cartItemResponses.get(0).getCartItemId();
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(3);

        final LoginRequest otherLoginRequest = new LoginRequest("other_test_email@woowafriends.com",
            "test_password!");
        String otherAccessToken = TestUtils.login(otherLoginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(otherAccessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemUpdateRequest)
            .when().put("/api/v1/cart/items/{cartItemId}/quantity", cartItemId)
            .then().log().all()
            .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_CART_ITEM);
    }

    @Test
    @DisplayName("실패 : 수량 정보 없이 장바구니 상품 수량을 수정한다.")
    void updateQuantityWithoutQuantity() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        TestUtils.insertCartItem(accessToken, cartItemInsertRequest);
        final List<CartItemResponse> cartItemResponses = TestUtils.readCartItems(accessToken)
            .jsonPath()
            .getList(".", CartItemResponse.class);

        final Long cartItemId = cartItemResponses.get(0).getCartItemId();
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(null);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemUpdateRequest)
            .when().put("/api/v1/cart/items/{cartItemId}/quantity", cartItemId)
            .then().log().all()
            .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD_MISSING);
    }

    @Test
    @DisplayName("실패 : 장바구니에 없는 상품의 수량을 수정할 수 없다.")
    void updateWithInvalidCartItemId() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final Long invalidCartItemId = Long.MAX_VALUE;
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(3);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemUpdateRequest)
            .when().put("/api/v1/cart/items/{cartItemId}/quantity", invalidCartItemId)
            .then().log().all()
            .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_CART_ITEM);
    }

    @Test
    @DisplayName("성공 : 장바구니에서 하나의 상품을 삭제한다")
    void successRemoveCartItem() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = TestUtils.login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        TestUtils.insertCartItem(accessToken, cartItemInsertRequest);
        final List<CartItemResponse> cartItemResponses = TestUtils.readCartItems(accessToken)
            .jsonPath()
            .getList(".", CartItemResponse.class);

        final Long cartItemId = cartItemResponses.get(0).getCartItemId();

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().delete("/api/v1/cart/items/{cartItemId}", cartItemId)
            .then().log().all()
            .extract();

        /* then */
        final List<CartItemResponse> updatedCartItemResponses = TestUtils.readCartItems(accessToken)
            .jsonPath()
            .getList(".", CartItemResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(updatedCartItemResponses).isEmpty();
    }
}
