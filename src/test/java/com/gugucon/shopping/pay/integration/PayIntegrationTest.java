package com.gugucon.shopping.pay.integration;

import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

@DisplayName("결제 인수 테스트")
class PayIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("결제 데이터 검증을 요청한다.")
    void createPayment() {
        // given
        PayRequest payRequest = new PayRequest(1L, 1000L);

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
        assertThat(payResponse)
                .extracting(PayResponse::getEncodedOrderId, PayResponse::getOrderName)
                .containsExactly(String.valueOf(payRequest.getOrderId()), String.valueOf(payRequest.getOrderId()));
    }

    @Test
    @DisplayName("결제 완료 후 검증 요청이 성공한다.")
    void validatePaymentSuccess() {
        // given
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("paymentKey", "test");
        queryParam.put("orderId", "testOrderId");
        queryParam.put("amount", 10000);
        queryParam.put("paymentType", "NORMAL");

        // when & then
        RestAssured
                .given().log().all()
                .queryParams(queryParam)
                .when().get("/pay/success")
                .then().log().all()
                .body(containsString("결제"), containsString("성공"));
    }

    @Test
    @DisplayName("결제 완료 후 검증 요청이 실패한다.")
    void validatePaymentFail() {
        // given
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("code", "PAY_PROCESS_CANCELED");
        queryParam.put("message", "사용자에 의해 결제가 취소되었습니다.");
        queryParam.put("orderId", "testOrderId");

        // when & then
        RestAssured
                .given().log().all()
                .queryParams(queryParam)
                .when().get("/pay/fail")
                .then().log().all()
                .body(containsString("결제"), containsString("실패"));
    }
}
