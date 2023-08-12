package com.gugucon.shopping.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static com.gugucon.shopping.TestUtils.extractCartItemResponses;
import static com.gugucon.shopping.TestUtils.extractOrderId;
import static com.gugucon.shopping.TestUtils.insertCartItem;
import static com.gugucon.shopping.TestUtils.login;
import static com.gugucon.shopping.TestUtils.placeOrder;
import static com.gugucon.shopping.TestUtils.readCartItems;

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
import com.gugucon.shopping.user.dto.request.LoginRequest;
import com.gugucon.shopping.user.dto.response.LoginResponse;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.order.dto.response.OrderDetailResponse;
import com.gugucon.shopping.order.dto.response.OrderHistoryResponse;
import com.gugucon.shopping.order.dto.response.OrderItemResponse;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.order.repository.OrderItemRepository;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;

@DisplayName("주문 기능 통합 테스트")
class OrderIntegrationTest extends IntegrationTest {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        orderRepository.deleteAll();
        orderItemRepository.deleteAll();
    }

    @Test
    @DisplayName("성공 : 주문한다.")
    void orderSuccess() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        insertCartItem(accessToken, cartItemInsertRequest);

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
    @DisplayName("실패 : 빈 장바구니로 주문한다.")
    void orderWithEmptyCart() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

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
    @DisplayName("성공 : 주문 상세 정보를 조회한다.")
    void readOrderDetailSuccess() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest1 = new CartItemInsertRequest(1L);
        insertCartItem(accessToken, cartItemInsertRequest1);

        final CartItemInsertRequest cartItemInsertRequest2 = new CartItemInsertRequest(2L);
        insertCartItem(accessToken, cartItemInsertRequest2);

        final List<CartItemResponse> cartItemResponses = extractCartItemResponses(
            readCartItems(accessToken));
        final List<String> cartItemNames = cartItemResponses.stream()
            .map(CartItemResponse::getName)
            .collect(Collectors.toUnmodifiableList());

        final Long orderId = extractOrderId(placeOrder(accessToken));

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
        final List<String> orderItemNames = orderDetailResponse.getOrderItems().stream()
            .map(OrderItemResponse::getProductName)
            .collect(Collectors.toUnmodifiableList());
        assertThat(cartItemNames).containsExactlyInAnyOrderElementsOf(orderItemNames);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("실패 : 존재하지 않는 주문을 조회한다.")
    void readInvalidOrderDetail() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();
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
    @DisplayName("실패 : 다른 사용자의 주문을 조회할 수 없다.")
    void readOrderDetailOfOtherUser() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        insertCartItem(accessToken, cartItemInsertRequest);

        final Long orderId = extractOrderId(placeOrder(accessToken));

        final LoginRequest otherLoginRequest = new LoginRequest("other_test_email@woowafriends.com",
            "test_password!");
        String otherAccessToken = login(otherLoginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

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

    @Test
    @DisplayName("성공 : 주문 목록을 조회한다.")
    void readOrderHistory() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
            "test_password!");
        String accessToken = login(loginRequest)
            .as(LoginResponse.class)
            .getAccessToken();

        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);
        insertCartItem(accessToken, cartItemInsertRequest);
        final Long firstOrderId = extractOrderId(placeOrder(accessToken));

        insertCartItem(accessToken, cartItemInsertRequest);
        final Long secondOrderId = extractOrderId(placeOrder(accessToken));

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when()
            .get("/api/v1/order-history")
            .then()
            .extract();

        /* then */
        final List<OrderHistoryResponse> orderHistoryResponse = response.jsonPath()
            .getList(".", OrderHistoryResponse.class);
        final List<Long> orderIds = orderHistoryResponse.stream()
            .map(OrderHistoryResponse::getOrderId)
            .collect(Collectors.toUnmodifiableList());
        assertThat(orderIds).containsExactly(secondOrderId, firstOrderId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
