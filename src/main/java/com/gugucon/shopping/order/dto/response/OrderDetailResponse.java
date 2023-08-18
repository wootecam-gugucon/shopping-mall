package com.gugucon.shopping.order.dto.response;

import com.gugucon.shopping.order.domain.entity.Order;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class OrderDetailResponse {

    private Long orderId;
    private List<OrderItemResponse> orderItems;
    private String orderName;
    private long totalPrice;

    public static OrderDetailResponse from(final Order order) {
        final List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        return new OrderDetailResponse(order.getId(), orderItemResponses, order.getOrderName(),
                                       order.calculateTotalPrice().getValue());
    }
}
