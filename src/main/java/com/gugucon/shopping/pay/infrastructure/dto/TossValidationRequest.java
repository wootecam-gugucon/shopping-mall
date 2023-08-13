package com.gugucon.shopping.pay.infrastructure.dto;

import com.gugucon.shopping.pay.dto.PaySuccessParameter;

public final class TossValidationRequest {

    private final String paymentKey;
    private final String orderId;
    private final int amount;

    public TossValidationRequest(final String paymentKey, final String orderId, final int amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public static TossValidationRequest of(PaySuccessParameter paySuccessParameter) {
        return new TossValidationRequest(paySuccessParameter.getPaymentKey(),
                                         paySuccessParameter.getOrderId(),
                                         paySuccessParameter.getPrice());
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getAmount() {
        return amount;
    }
}
