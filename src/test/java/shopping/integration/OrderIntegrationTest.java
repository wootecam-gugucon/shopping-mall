package shopping.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import shopping.TestUtils;
import shopping.auth.dto.request.LoginRequest;
import shopping.auth.dto.response.LoginResponse;
import shopping.cart.dto.request.CartItemInsertRequest;
import shopping.cart.repository.CartItemRepository;
import shopping.cart.repository.OrderItemRepository;
import shopping.cart.repository.OrderRepository;

@DisplayName("주문 기능 통합 테스트")
public class OrderIntegrationTest extends IntegrationTest {

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
    @DisplayName("주문에 성공한다.")
    void orderSuccess() {
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
            .when()
            .post("/order")
            .then()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }
}
