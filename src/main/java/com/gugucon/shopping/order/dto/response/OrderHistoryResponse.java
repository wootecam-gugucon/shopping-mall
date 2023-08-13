package com.gugucon.shopping.order.dto.response;

import com.gugucon.shopping.order.domain.entity.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class OrderHistoryResponse {

    private Long orderId;
    private List<OrderItemResponse> orderItems;

    public static OrderHistoryResponse from(final Order order) {
        final List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        return new OrderHistoryResponse(order.getId(), orderItemResponses);
    }
}
