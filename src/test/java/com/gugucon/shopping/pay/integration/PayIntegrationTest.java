package com.gugucon.shopping.pay.integration;

import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("결제 인수 테스트")
class PayIntegrationTest extends IntegrationTest {

    @Autowired
    private OrderIdTranslator orderIdTranslator;

//    @Test
//    @DisplayName("결제 데이터 검증을 요청한다.")
//    void createPayment() {
//        // given
//        Long orderId = 1L;
//        String orderName = "대충 주문 이름";
//        PayCreateRequest payCreateRequest = new PayCreateRequest(1L);
//
//        // when
//        ExtractableResponse<Response> response = RestAssured
//                .given().log().all()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .body(payCreateRequest)
//                .when().post("/api/pay")
//                .then().log().all()
//                .extract();
//
//        // then
//        PayInfoResponse payInfoResponse = response.as(PayInfoResponse.class);
//        assertThat(orderIdTranslator.decode(payInfoResponse.getEncodedOrderId())).isEqualTo(orderId);
//        assertThat(payInfoResponse.getOrderName()).isEqualTo(orderName);
//    }
}
