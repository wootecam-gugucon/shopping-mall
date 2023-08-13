package com.gugucon.shopping.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PayRequest {

    private final Long orderId;
    private final Long price;
    private final String orderName;
}
