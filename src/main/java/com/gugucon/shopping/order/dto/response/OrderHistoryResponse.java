package com.gugucon.shopping.order.dto.response;

import com.gugucon.shopping.order.domain.entity.Order;

import java.util.List;

public class OrderHistoryResponse {

    private Long orderId;
    private List<OrderItemResponse> orderItems;

    private OrderHistoryResponse() {
    }

    private OrderHistoryResponse(final Long orderId, final List<OrderItemResponse> orderItems) {
        this.orderId = orderId;
        this.orderItems = orderItems;
    }

    public static OrderHistoryResponse from(final Order order) {
        final List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        return new OrderHistoryResponse(order.getId(), orderItemResponses);
    }

    public Long getOrderId() {
        return orderId;
    }

    public List<OrderItemResponse> getOrderItems() {
        return orderItems;
    }
}
