package com.gugucon.shopping.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PaySuccessParameter {

    private final String paymentKey;
    private final String orderId;
    private final Long price;
    private final String paymentType;
}
