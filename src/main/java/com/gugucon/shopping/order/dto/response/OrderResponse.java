package com.gugucon.shopping.order.dto.response;

import com.gugucon.shopping.order.domain.entity.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class OrderResponse {

    private Long orderId;

    public static OrderResponse from(final Order order) {
        return new OrderResponse(order.getId());
    }
}
