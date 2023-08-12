package com.gugucon.shopping.pay.dto;

public final class PayRequest {

    private final Long orderId;
    private final Long price;

    public PayRequest(final Long orderId, final Long price) {
        this.orderId = orderId;
        this.price = price;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getPrice() {
        return price;
    }
}
