package com.gugucon.shopping.pay.integration;

import static com.gugucon.shopping.TestUtils.insertCartItem;
import static com.gugucon.shopping.TestUtils.login;
import static com.gugucon.shopping.TestUtils.placeOrder;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@DisplayName("결제 인수 테스트")
class PayIntegrationTest extends IntegrationTest {

    @Autowired
    private OrderIdTranslator orderIdTranslator;

    @Test
    @DisplayName("결제 데이터 검증을 요청한다.")
    void createPayment() {
        // given
        final Long orderId = 1L;
        final String orderName = "대충 주문 이름";
        final PayRequest payRequest = new PayRequest(1L, 1000L, orderName);
        final String accessToken = login(new LoginRequest("test_email@woowafriends.com",
                                                          "test_password!"));

        insertCartItem(accessToken, new CartItemInsertRequest(1L));
        placeOrder(accessToken);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(payRequest)
                .when().post("/api/pay")
                .then().log().all()
                .extract();

        // then
        final PayResponse payResponse = response.as(PayResponse.class);
        assertThat(orderIdTranslator.decode(payResponse.getEncodedOrderId())).isEqualTo(orderId);
        assertThat(payResponse.getOrderName()).isEqualTo(orderName);
    }
}
