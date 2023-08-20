package com.gugucon.shopping.pay.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PayFailRequest {

    private final String code;
    private final String message;
    private final String orderId;
}
