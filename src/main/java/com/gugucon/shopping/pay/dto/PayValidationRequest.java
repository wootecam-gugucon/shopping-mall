package com.gugucon.shopping.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PayValidationRequest {

    private final String paymentKey;
    private final String orderId;
    private final Long amount;
    private final String paymentType;
}
