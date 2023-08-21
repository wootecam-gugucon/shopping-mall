package com.gugucon.shopping.pay.infrastructure.dto;

import com.gugucon.shopping.pay.dto.toss.request.TossPayValidationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class TossValidationRequest {

    private final String paymentKey;
    private final String orderId;
    private final Long amount;

    public static TossValidationRequest of(final TossPayValidationRequest tossPayValidationRequest) {
        return new TossValidationRequest(tossPayValidationRequest.getPaymentKey(),
                                         tossPayValidationRequest.getOrderId(),
                                         tossPayValidationRequest.getAmount());
    }
}
