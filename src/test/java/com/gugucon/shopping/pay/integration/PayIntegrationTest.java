package com.gugucon.shopping.pay.integration;

import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("결제 인수 테스트")
class PayIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("결제 데이터 검증을 요청한다.")
    void payment() {
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
}
