package com.gugucon.shopping.integration;

import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.order.domain.PayType;
import com.gugucon.shopping.order.dto.request.OrderPayRequest;
import com.gugucon.shopping.order.dto.response.OrderDetailResponse;
import com.gugucon.shopping.order.dto.response.OrderItemResponse;
import com.gugucon.shopping.utils.DomainUtils;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static com.gugucon.shopping.utils.ApiUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("주문 기능 통합 테스트")
class OrderIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    private static List<String> toNames(final OrderDetailResponse orderDetailResponse) {
        return orderDetailResponse.getOrderItems().stream()
                .map(OrderItemResponse::getName)
                .toList();
    }

    @Test
    @DisplayName("주문한다.")
    void order() {
        /* given */
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        addProductToCart(accessToken, "testProduct");

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .post("/api/v1/order")
                .then()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("장바구니가 비어 있으면 주문을 요청했을 때 400 상태코드를 응답한다.")
    void orderFail_emptyCart() {
        /* given */
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .post("/api/v1/order")
                .then()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMPTY_CART);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("품절된 상품을 포함해 주문을 요청했을 때 400 상태코드를 응답한다.")
    void orderFail_soldOutProduct() {
        /* given */
        final String email = "test_email@woowafriends.com";
        final String accessToken = loginAfterSignUp(email, "test_password!");

        final Long productId = insertProduct("testProduct", 1000);
        insertCartItem(accessToken, new CartItemInsertRequest(productId));
        changeProductStock(productId, 0);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .post("/api/v1/order")
                .then()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.SOLD_OUT);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("주문을 요청했을 때 재고가 부족하면 400 상태코드를 응답한다.")
    void orderFail_lackOfStock() {
        /* given */
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");

        final Long productId = insertProduct("testProduct", 1000);
        insertCartItem(accessToken, new CartItemInsertRequest(productId));
        updateCartItemQuantity(accessToken, 100);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/v1/order")
                .then()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.STOCK_NOT_ENOUGH);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("주문 상세 정보를 조회한다.")
    void readOrderDetail() {
        /* given */
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");

        addProductToCart(accessToken, "chicken");
        addProductToCart(accessToken, "pizza");

        final Long orderId = placeOrder(accessToken);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .get("/api/v1/order/{orderId}", orderId)
                .then()
                .extract();

        /* then */
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
        final OrderDetailResponse orderDetailResponse = response.as(OrderDetailResponse.class);
        assertThat(toNames(orderDetailResponse)).containsExactlyInAnyOrderElementsOf(toNames(cartItemResponses));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 주문이면 주문 상세정보 조회를 요청했을 때 404 상태코드를 응답한다.")
    void readOrderDetailFail_invalidOrderId() {
        /* given */
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");

        final Long invalidOrderId = Long.MAX_VALUE;

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .get("/api/v1/order/{orderId}", invalidOrderId)
                .then()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("다른 사용자의 주문이면 주문 상세정보 조회를 요청했을 때 404 상태코드를 응답한다.")
    void readOrderDetailFail_orderOfOtherUser() {
        /* given */
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");

        addProductToCart(accessToken, "testProduct");
        final Long orderId = placeOrder(accessToken);

        final String otherAccessToken = loginAfterSignUp("other_test_email@woowafriends.com", "test_password!");

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(otherAccessToken)
                .when()
                .get("/api/v1/order/{orderId}", orderId)
                .then()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("결제를 요청한다.")
    void requestPay() {
        /* given */
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        addProductToCart(accessToken, "testProduct");
        Long orderId = placeOrder(accessToken);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new OrderPayRequest(orderId, PayType.POINT))
                .when()
                .put("/api/v1/order")
                .then()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("결제를 요청했을 때 재고가 없으면 400 상태를 반환한다.")
    void requestPay_invalidStock_status400() {
        /* given */
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        Long productId = insertProduct("testProduct", 1000L);
        insertCartItem(accessToken, new CartItemInsertRequest(productId));
        Long orderId = placeOrder(accessToken);
        changeProductStock(productId, 0);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new OrderPayRequest(orderId, PayType.POINT))
                .when()
                .put("/api/v1/order")
                .then()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.STOCK_NOT_ENOUGH);
    }

    private List<String> toNames(final List<CartItemResponse> cartItemResponses) {
        return cartItemResponses.stream()
                .map(CartItemResponse::getName)
                .toList();
    }

    private void updateCartItemQuantity(String accessToken, int quantity) {
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
        final Long cartItemId = cartItemResponses.get(0).getCartItemId();
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(quantity);
        updateCartItem(accessToken, cartItemId, cartItemUpdateRequest);
    }

    private void changeProductStock(Long productId, int stock) {
        final Product product = productRepository.findById(productId).orElseThrow();
        final Product updatedProduct = Product.builder()
                .id(productId)
                .price(product.getPrice())
                .name(product.getName())
                .description(product.getDescription())
                .imageFileName(product.getImageFileName())
                .stock(Quantity.from(stock))
                .build();
        productRepository.save(updatedProduct);
    }

    private void addProductToCart(final String accessToken,
                                  final String productName) {
        final Long productId = insertProduct(productName, 1000L);
        insertCartItem(accessToken, new CartItemInsertRequest(productId));
    }

    private Long insertProduct(final String productName,
                               final long price) {
        final Product product = DomainUtils.createProductWithoutId(productName, price, 10);
        productRepository.save(product);
        return product.getId();
    }
}
