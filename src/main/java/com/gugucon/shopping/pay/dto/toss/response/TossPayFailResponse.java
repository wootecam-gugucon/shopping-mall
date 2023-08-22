package com.gugucon.shopping.pay.dto.toss.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TossPayFailResponse {

    private Long orderId;

    public static TossPayFailResponse from(Long orderId) {
        return new TossPayFailResponse(orderId);
    }
}