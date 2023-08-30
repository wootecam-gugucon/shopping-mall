package com.gugucon.shopping.pay.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class TossPayRequest {

    private final String paymentKey;
    private final String orderId;
    private final Long amount;
    private final String paymentType;
}
