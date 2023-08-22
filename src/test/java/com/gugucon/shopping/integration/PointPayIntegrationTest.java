package com.gugucon.shopping.integration;

import static com.gugucon.shopping.utils.ApiUtils.chargePoint;
import static com.gugucon.shopping.utils.ApiUtils.insertCartItem;
import static com.gugucon.shopping.utils.ApiUtils.loginAfterSignUp;
import static com.gugucon.shopping.utils.ApiUtils.placeOrder;
import static com.gugucon.shopping.utils.ApiUtils.readCartItems;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.pay.dto.point.request.PointPayRequest;
import com.gugucon.shopping.pay.dto.point.response.PointPayResponse;
import com.gugucon.shopping.utils.DomainUtils;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@IntegrationTest
@DisplayName("포인트 결제 기능 통합 테스트")
class PointPayIntegrationTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("포인트로 주문하면 결제 정보를 생성한다.")
    void createPayment_() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        chargePoint(accessToken, 2000L);
        addProductToCart(accessToken, "testProduct", 1000L);
        final Long orderId = placeOrder(accessToken);
        final PointPayRequest pointPayRequest = new PointPayRequest(orderId);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(pointPayRequest)
                .when()
                .put("/api/v1/pay/point")
                .then().log().all()
                .extract();

        // then
        final PointPayResponse pointPayResponse = response.as(PointPayResponse.class);
        assertThat(pointPayResponse.getOrderId()).isEqualTo(orderId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(readCartItems(accessToken)).isEmpty();
        assertThat(orderRepository.findById(orderId)).isPresent()
                                                     .get()
                                                     .extracting(Order::getStatus)
                                                     .isEqualTo(OrderStatus.PAYED);
    }

    @Test
    @DisplayName("포인트로 주문할 때 가격이 포인트 잔액보다 크면 400 상태코드를 응답한다.")
    void createPaymentFail_priceOverPointBalance() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        chargePoint(accessToken, 2000L);
        addProductToCart(accessToken, "testProduct", 3000L);
        final Long orderId = placeOrder(accessToken);
        final PointPayRequest pointPayRequest = new PointPayRequest(orderId);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(pointPayRequest)
                .when()
                .put("/api/v1/pay/point")
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.POINT_NOT_ENOUGH);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void addProductToCart(final String accessToken, final String productName, final Long price) {
        final Long productId = insertProduct(productName, price);
        insertCartItem(accessToken, new CartItemInsertRequest(productId));
    }

    private Long insertProduct(final String productName, final Long price) {
        final Product product = DomainUtils.createProductWithoutId(productName, price, 10);
        productRepository.save(product);
        return product.getId();
    }
}
