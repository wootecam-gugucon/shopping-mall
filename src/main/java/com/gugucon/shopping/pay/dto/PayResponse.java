package com.gugucon.shopping.pay.dto;

import com.gugucon.shopping.pay.domain.Pay;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PayResponse {

    private static final String customerEmail = "asdf@asdf.asdf";
    private static final String customerName = "김동주";

    private final String encodedOrderId;
    private final String orderName;
    private final String successUrl;
    private final String failUrl;

    public static PayResponse from(final Pay pay, final String successUrl, final String failUrl) {
        return new PayResponse(pay.getEncodedOrderId(), pay.getOrderName(), successUrl, failUrl);
    }
}
