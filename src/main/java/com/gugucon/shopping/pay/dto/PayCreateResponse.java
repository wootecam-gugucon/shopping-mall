package com.gugucon.shopping.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PayCreateResponse {

    private final Long payId;

    public static PayCreateResponse from(final Long payId) {
        return new PayCreateResponse(payId);
    }
}
