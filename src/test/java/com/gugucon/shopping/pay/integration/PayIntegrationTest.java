package com.gugucon.shopping.pay.integration;

import static org.assertj.core.api.Assertions.assertThat;

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
        Long orderId = 1L;
        String orderName = "대충 주문 이름";
        PayRequest payRequest = new PayRequest(1L, 1000L, orderName);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(payRequest)
                .when().post("/api/pay")
                .then().log().all()
                .extract();

        // then
        PayResponse payResponse = response.as(PayResponse.class);
        assertThat(orderIdTranslator.decode(payResponse.getEncodedOrderId())).isEqualTo(orderId);
        assertThat(payResponse.getOrderName()).isEqualTo(orderName);
    }
}