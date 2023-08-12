package com.gugucon.shopping.order.dto.response;

import com.gugucon.shopping.order.domain.entity.Order;

import java.util.List;

public final class OrderDetailResponse {

    private Long orderId;
    private List<OrderItemResponse> orderItems;
    private long totalPrice;
    private double dollarTotalPrice;
    private double exchangeRate;

    private OrderDetailResponse() {
    }

    private OrderDetailResponse(final Long orderId, final List<OrderItemResponse> orderItems,
                                final long totalPrice, final double dollarTotalPrice, final double exchangeRate) {
        this.orderId = orderId;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        this.dollarTotalPrice = dollarTotalPrice;
        this.exchangeRate = exchangeRate;
    }

    public static OrderDetailResponse from(final Order order) {
        final List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        return new OrderDetailResponse(order.getId(), orderItemResponses,
                order.getTotalPrice().getValue(), order.getTotalPriceInDollar().getValue(),
                order.getExchangeRate().getValue());
    }

    public Long getOrderId() {
        return orderId;
    }

    public List<OrderItemResponse> getOrderItems() {
        return orderItems;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public double getDollarTotalPrice() {
        return dollarTotalPrice;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }
}
