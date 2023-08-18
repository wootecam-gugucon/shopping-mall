package com.gugucon.shopping.pay.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PayValidationResponse {

    private Long orderId;

    public static PayValidationResponse from(Long orderId) {
        return new PayValidationResponse(orderId);
    }
}
