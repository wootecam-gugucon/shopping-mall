package com.gugucon.shopping.order.dto.response;

import com.gugucon.shopping.order.domain.entity.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access =  AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderPayResponse {

    private Long orderId;
    private String payType;

    public static OrderPayResponse from(final Order order) {
        return new OrderPayResponse(order.getId(), order.getPayType().toString());
    }
}
