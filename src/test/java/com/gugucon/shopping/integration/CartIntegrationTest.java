package com.gugucon.shopping.integration;

import static com.gugucon.shopping.utils.ApiUtils.insertCartItem;
import static com.gugucon.shopping.utils.ApiUtils.readCartItems;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.utils.ApiUtils;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@IntegrationTest
@DisplayName("장바구니 기능 통합 테스트")
class CartIntegrationTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("장바구니에 상품을 추가한다.")
    void insertCartItem_() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

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
    @DisplayName("장바구니에 같은 상품이 들어 있으면 장바구니 상품 추가를 요청했을 때 400 상태코드를 응답한다.")
    void insertCartItemFail_duplicateItem() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new CartItemInsertRequest(1L))
                .when().post("/api/v1/cart/items")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_CART_ITEM);
    }

    @Test
    @DisplayName("productId가 없으면 장바구니 상품 추가를 요청했을 때 400 상태코드를 응답한다.")
    void insertCartItemFail_withoutProductId() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

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
    @DisplayName("존재하지 않는 상품이면 장바구니 상품 추가를 요청했을 때 400 상태코드를 응답한다.")
    void insertCartItemFail_invalidProduct() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        final Long invalidProductId = Long.MAX_VALUE;
        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(invalidProductId);

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
    @DisplayName("품절된 상품이면 장바구니 상품 추가를 요청했을 때 400 상태코드를 응답한다.")
    void insertCartItemFail_soldOutProduct() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        final Long soldOutProductId = 4L;
        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(soldOutProductId);

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
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.SOLD_OUT);
    }

    @Test
    @DisplayName("장바구니 상품 목록을 조회한다")
    void readCartItems_() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        insertCartItem(accessToken, new CartItemInsertRequest(2L));

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/v1/cart/items")
                .then().log().all()
                .extract();

        /* then */
        final List<CartItemResponse> cartItemResponses = response.jsonPath()
                .getList(".", CartItemResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(toProductNames(cartItemResponses)).containsExactly("치킨", "피자");
        assertThat(cartItemResponses).hasSize(2);
    }

    @Test
    @DisplayName("장바구니 상품 하나의 수량을 수정한다.")
    void updateCartItemQuantity() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
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
        final List<CartItemResponse> updatedCartItemResponses = readCartItems(accessToken);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(updatedCartItemResponses.get(0).getQuantity()).isEqualTo(cartItemUpdateRequest.getQuantity());
    }

    @Test
    @DisplayName("장바구니 상품 수량을 0으로 수정하면 삭제된다.")
    void updateCartItemQuantityToZero() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
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
        final List<CartItemResponse> updatedCartItemResponses = readCartItems(accessToken);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(updatedCartItemResponses).isEmpty();
    }

    @Test
    @DisplayName("변경 수량이 0 미만이면 장바구니 상품 수량 변경을 요청했을 때 400 상태코드를 응답한다.")
    void updateCartItemQuantityFail_quantityUnderZero() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
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
    @DisplayName("다른 사용자의 장바구니 상품이면 장바구니 상품 수량 변경을 요청했을 때 400 상태코드를 응답한다.")
    void updateCartItemQuantityFail_cartItemOfOtherUser() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
        final Long cartItemId = cartItemResponses.get(0).getCartItemId();
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(3);

        final String otherAccessToken = signUpAndLogin("other_test_email@woowafriends.com", "test_password!");

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
    @DisplayName("변경 수량 정보가 없으면 장바구니 상품 수량 변경을 요청했을 때 400 상태코드를 응답한다.")
    void updateCartItemQuantityFail_withoutQuantity() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
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
    @DisplayName("존재하지 않는 장바구니 상품이면 장바구니 상품 수량 변경을 요청했을 때 400 상태코드를 응답한다.")
    void updateCartItemQuantityFail_invalidCartItemId() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

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
    @DisplayName("장바구니 상품을 삭제한다.")
    void removeCartItem() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
        final Long cartItemId = cartItemResponses.get(0).getCartItemId();

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/api/v1/cart/items/{cartItemId}", cartItemId)
                .then().log().all()
                .extract();

        /* then */
        final List<CartItemResponse> updatedCartItemResponses = readCartItems(accessToken);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(updatedCartItemResponses).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 장바구니 상품을 삭제할 때 400 상태코드를 응답한다.")
    void removeCartItem_productNotExist() {
        /* given */
        final String accessToken = signUpAndLogin("test_email@woowafriends.com", "test_password!");

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/api/v1/cart/items/{cartItemId}", 1000)
                .then().log().all()
                .extract();

        /* then */
        final List<CartItemResponse> updatedCartItemResponses = readCartItems(accessToken);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(updatedCartItemResponses).isEmpty();
    }

    private List<String> toProductNames(final List<CartItemResponse> cartItemResponses) {
        return cartItemResponses.stream()
                .map(CartItemResponse::getName)
                .toList();
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
