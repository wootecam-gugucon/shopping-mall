package com.gugucon.shopping.integration;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.order.dto.response.OrderDetailResponse;
import com.gugucon.shopping.order.dto.response.OrderHistoryResponse;
import com.gugucon.shopping.order.dto.response.OrderItemResponse;
import com.gugucon.shopping.order.repository.OrderItemRepository;
import com.gugucon.shopping.order.repository.OrderRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.gugucon.shopping.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 기능 통합 테스트")
class OrderIntegrationTest extends IntegrationTest {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    private static List<String> toProductNames(final OrderDetailResponse orderDetailResponse) {
        return orderDetailResponse.getOrderItems().stream()
                .map(OrderItemResponse::getProductName)
                .toList();
    }

    private static List<Long> toOrderIds(final List<OrderHistoryResponse> orderHistoryResponse) {
        return orderHistoryResponse.stream()
                .map(OrderHistoryResponse::getOrderId)
                .toList();
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        orderRepository.deleteAll();
        orderItemRepository.deleteAll();
    }

    @Test
    @DisplayName("주문한다.")
    void order() {
        /* given */
        String accessToken = login(new LoginRequest("test_email@woowafriends.com", "test_password!"));
        insertCartItem(accessToken, new CartItemInsertRequest(1L));

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
        String accessToken = login(new LoginRequest("test_email@woowafriends.com", "test_password!"));

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
    @DisplayName("주문 상세 정보를 조회한다.")
    void readOrderDetail() {
        /* given */
        String accessToken = login(new LoginRequest("test_email@woowafriends.com", "test_password!"));
        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        insertCartItem(accessToken, new CartItemInsertRequest(2L));
        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
        final List<String> cartItemNames = toProductNames(cartItemResponses);
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
        final OrderDetailResponse orderDetailResponse = response.as(OrderDetailResponse.class);
        assertThat(toProductNames(orderDetailResponse)).containsExactlyInAnyOrderElementsOf(cartItemNames);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 주문이면 주문 상세정보 조회를 요청했을 때 400 상태코드를 응답한다.")
    void readOrderDetailFail_invalidOrderId() {
        /* given */
        String accessToken = login(new LoginRequest("test_email@woowafriends.com", "test_password!"));
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("다른 사용자의 주문이면 주문 상세정보 조회를 요청했을 때 400 상태코드를 응답한다.")
    void readOrderDetailFail_orderOfOtherUser() {
        /* given */
        String accessToken = login(new LoginRequest("test_email@woowafriends.com", "test_password!"));
        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final Long orderId = placeOrder(accessToken);
        String otherAccessToken = login(new LoginRequest("other_test_email@woowafriends.com", "test_password!"));

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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private List<String> toProductNames(final List<CartItemResponse> cartItemResponses) {
        return cartItemResponses.stream()
                .map(CartItemResponse::getName)
                .toList();
    }
}
