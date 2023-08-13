package com.gugucon.shopping.pay.infrastructure.dto;

import com.gugucon.shopping.pay.dto.PaySuccessParameter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class TossValidationRequest {

    private final String paymentKey;
    private final String orderId;
    private final int amount;

    public static TossValidationRequest of(PaySuccessParameter paySuccessParameter) {
        return new TossValidationRequest(paySuccessParameter.getPaymentKey(),
                                         paySuccessParameter.getOrderId(),
                                         paySuccessParameter.getPrice());
    }
}
