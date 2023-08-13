package com.gugucon.shopping.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PayFailParameter {

    private final String errorCode;
    private final String message;
    private final String orderId;
}