package com.gugucon.shopping.order.dto.response;

import com.gugucon.shopping.order.domain.entity.OrderItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class OrderItemResponse {

    private String name;
    private long price;
    private String imageFileName;
    private int quantity;

    public static OrderItemResponse from(final OrderItem orderItem) {
        return new OrderItemResponse(orderItem.getName(), orderItem.getPrice().getValue(),
                                     orderItem.getImageFileName(), orderItem.getQuantity().getValue());
    }
}
