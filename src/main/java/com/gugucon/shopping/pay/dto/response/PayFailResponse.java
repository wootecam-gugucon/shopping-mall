package com.gugucon.shopping.pay.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PayFailResponse {

    private Long orderId;

    public static PayFailResponse from(Long orderId) {
        return new PayFailResponse(orderId);
    }
}
