package com.gugucon.shopping.cart.dto.response;

import com.gugucon.shopping.cart.domain.entity.Order;

public final class OrderResponse {

    private Long orderId;

    private OrderResponse(final Long orderId) {
        this.orderId = orderId;
    }

    public static OrderResponse from(final Order order) {
        return new OrderResponse(order.getId());
    }

    public Long getOrderId() {
        return orderId;
    }
}
