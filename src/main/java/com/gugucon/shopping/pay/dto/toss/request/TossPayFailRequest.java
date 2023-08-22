package com.gugucon.shopping.pay.dto.toss.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class TossPayFailRequest {

    private final String code;
    private final String message;
    private final String orderId;
}
