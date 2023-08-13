package com.gugucon.shopping.pay.dto;

public final class PayRequest {

    private final Long orderId;
    private final Long price;
    private final String orderName;

    public PayRequest(final Long orderId, final Long price, String orderName) {
        this.orderId = orderId;
        this.price = price;
        this.orderName = orderName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getPrice() {
        return price;
    }

    public String getOrderName() {
        return orderName;
    }
}
