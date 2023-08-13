package com.gugucon.shopping.pay.dto;

public final class PaySuccessParameter {

    private final String paymentKey;
    private final String orderId;
    private final int price;
    private final String paymentType;

    public PaySuccessParameter(final String paymentKey,
                               final String orderId,
                               final int price,
                               final String paymentType) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.price = price;
        this.paymentType = paymentType;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getPrice() {
        return price;
    }

    public String getPaymentType() {
        return paymentType;
    }
}
