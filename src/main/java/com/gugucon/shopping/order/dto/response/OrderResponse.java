package com.gugucon.shopping.order.dto.response;

import com.gugucon.shopping.order.domain.entity.Order;

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
