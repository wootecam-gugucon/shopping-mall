package com.gugucon.shopping.pay.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TossPayFailResponse {

    private Long orderId;

    public static TossPayFailResponse from(final Long orderId) {
        return new TossPayFailResponse(orderId);
    }
}
