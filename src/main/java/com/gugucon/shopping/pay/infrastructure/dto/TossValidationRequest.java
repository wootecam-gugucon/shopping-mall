package com.gugucon.shopping.pay.infrastructure.dto;

import com.gugucon.shopping.pay.dto.request.TossPayRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class TossValidationRequest {

    private final String paymentKey;
    private final String orderId;
    private final Long amount;

    public static TossValidationRequest of(final TossPayRequest tossPayRequest) {
        return new TossValidationRequest(tossPayRequest.getPaymentKey(),
                                         tossPayRequest.getOrderId(),
                                         tossPayRequest.getAmount());
    }
}
