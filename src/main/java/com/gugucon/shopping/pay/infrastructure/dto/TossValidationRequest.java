package com.gugucon.shopping.pay.infrastructure.dto;

import com.gugucon.shopping.pay.dto.request.PayValidationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class TossValidationRequest {

    private final String paymentKey;
    private final String orderId;
    private final Long amount;

    public static TossValidationRequest of(final PayValidationRequest payValidationRequest) {
        return new TossValidationRequest(payValidationRequest.getPaymentKey(),
                                         payValidationRequest.getOrderId(),
                                         payValidationRequest.getAmount());
    }
}
