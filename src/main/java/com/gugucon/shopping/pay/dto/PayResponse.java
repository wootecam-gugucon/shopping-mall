package com.gugucon.shopping.pay.dto;

import com.gugucon.shopping.pay.domain.Pay;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PayResponse {

    private final String encodedOrderId;
    private final String orderName;
    private static final String successUrl = "http://localhost:8080/pay/loading";
    private static final String failUrl = "http://localhost:8080/pay/fail";
    private static final String customerEmail = "asdf@asdf.asdf";
    private static final String customerName = "김동주";

    public static PayResponse of(final Pay pay) {
        return new PayResponse(pay.getEncodedOrderId(), pay.getOrderName());
    }
}
