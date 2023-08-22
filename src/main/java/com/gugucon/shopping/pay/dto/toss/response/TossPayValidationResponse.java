package com.gugucon.shopping.pay.dto.toss.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TossPayValidationResponse {

    private Long orderId;

    public static TossPayValidationResponse from(final Long orderId) {
        return new TossPayValidationResponse(orderId);
    }
}
