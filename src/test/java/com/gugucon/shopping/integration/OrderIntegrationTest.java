package com.gugucon.shopping.integration;

import com.gugucon.shopping.TestUtils;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.order.dto.response.OrderDetailResponse;
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
import org.springframework.http.MediaType;

import java.util.List;

import static com.gugucon.shopping.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("주문 기능 통합 테스트")
class OrderIntegrationTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private static List<String> toNames(final OrderDetailResponse orderDetailResponse) {
        return orderDetailResponse.getOrderItems().stream()
                .map(OrderItemResponse::getName)
                .toList();
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("주문한다.")
    void order() {
        /* given */
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        final String nickname = "tester1";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);
        TestUtils.signup(signupRequest);
        String accessToken = TestUtils.login(new LoginRequest(email, password));
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
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        final String nickname = "tester1";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);
        TestUtils.signup(signupRequest);
        String accessToken = TestUtils.login(new LoginRequest(email, password));

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
        final String password = "test_password!";
        final String nickname = "tester1";
        TestUtils.signup(new SignupRequest(email, password, password, nickname));
        final String accessToken = TestUtils.login(new LoginRequest(email, password));

        final Long memberId = memberRepository.findByEmail(Email.from(email)).orElseThrow().getId();
        cartItemRepository.save(CartItem.builder()
                                        .product(productRepository.findById(4L).orElseThrow())
                                        .memberId(memberId)
                                        .quantity(Quantity.from(1))
                                        .build());

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
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        final String nickname = "tester1";
        TestUtils.signup(new SignupRequest(email, password, password, nickname));
        String accessToken = TestUtils.login(new LoginRequest(email, password));

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final Long cartItemId = readCartItems(accessToken).get(0).getCartItemId();
        updateCartItem(accessToken, cartItemId, new CartItemUpdateRequest(500));

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
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LACK_OF_STOCK);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("주문 상세 정보를 조회한다.")
    void readOrderDetail() {
        /* given */
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        final String nickname = "tester1";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);
        TestUtils.signup(signupRequest);
        String accessToken = TestUtils.login(new LoginRequest(email, password));

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        insertCartItem(accessToken, new CartItemInsertRequest(2L));

        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
        final List<String> cartItemNames = toNames(cartItemResponses);
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
        assertThat(toNames(orderDetailResponse)).containsExactlyInAnyOrderElementsOf(cartItemNames);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 주문이면 주문 상세정보 조회를 요청했을 때 404 상태코드를 응답한다.")
    void readOrderDetailFail_invalidOrderId() {
        /* given */
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        final String nickname = "tester1";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);
        TestUtils.signup(signupRequest);
        String accessToken = TestUtils.login(new LoginRequest(email, password));

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
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        final String nickname = "tester1";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);
        TestUtils.signup(signupRequest);
        String accessToken = TestUtils.login(new LoginRequest(email, password));

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        final Long orderId = placeOrder(accessToken);

        final String otherEmail = "other_test_email@woowafriends.com";
        final String otherPassword = "test_password!";
        final String otherNickname = "tester2";
        TestUtils.signup(new SignupRequest(otherEmail, otherPassword, otherPassword, otherNickname));
        String otherAccessToken = TestUtils.login(new LoginRequest(otherEmail, otherPassword));

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

    private List<String> toNames(final List<CartItemResponse> cartItemResponses) {
        return cartItemResponses.stream()
                .map(CartItemResponse::getName)
                .toList();
    }
}
