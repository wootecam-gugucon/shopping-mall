package shopping.cart.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import shopping.cart.domain.entity.Order;

public final class OrderDetailResponse {

    private Long orderId;
    private List<OrderItemResponse> orderItems;
    private long totalPrice;

    private OrderDetailResponse() {
    }

    private OrderDetailResponse(final Long orderId,
        final List<OrderItemResponse> orderItems,
        final long totalPrice) {
        this.orderId = orderId;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
    }

    public static OrderDetailResponse from(final Order order) {
        final List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
            .map(OrderItemResponse::from)
            .collect(Collectors.toUnmodifiableList());
        return new OrderDetailResponse(order.getId(), orderItemResponses,
            order.getTotalPrice().getValue());
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
}
