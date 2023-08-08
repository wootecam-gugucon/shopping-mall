package shopping.cart.dto.response;

import shopping.cart.domain.entity.Order;

public final class OrderCreateResponse {

    private Long orderId;

    private OrderCreateResponse(final Long orderId) {
        this.orderId = orderId;
    }

    public static OrderCreateResponse from(final Order order) {
        return new OrderCreateResponse(order.getId());
    }

    public Long getOrderId() {
        return orderId;
    }
}
