package com.gugucon.shopping.pay.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PayValidationResponse {

    private final Long orderId;

    public static PayValidationResponse from(Long orderId) {
        return new PayValidationResponse(orderId);
    }
}
