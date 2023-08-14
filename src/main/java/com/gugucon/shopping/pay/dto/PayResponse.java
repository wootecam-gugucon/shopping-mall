package com.gugucon.shopping.pay.dto;

import com.gugucon.shopping.pay.domain.Pay;
import lombok.Getter;

@Getter
public final class PayResponse {

    private final String encodedOrderId;
    private final String orderName;
    private final String successUrl = "http://localhost:8080/pay/loading";
    private final String failUrl = "http://localhost:8080/pay/fail";
    // TODO: 회원 정보 가져오기?
    private final String customerEmail = "asdf@asdf.asdf";
    private final String customerName = "김동주";

    private PayResponse(final String encodedOrderId, final String orderName) {
        this.encodedOrderId = encodedOrderId;
        this.orderName = orderName;
    }

    public static PayResponse of(final Pay pay) {
        return new PayResponse(pay.getEncodedOrderId(), pay.getOrderName());
    }
}
