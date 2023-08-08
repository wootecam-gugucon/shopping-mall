package shopping.cart.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import shopping.cart.domain.entity.Order;

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
            .collect(Collectors.toUnmodifiableList());
        return new OrderHistoryResponse(order.getId(), orderItemResponses);
    }

    public Long getOrderId() {
        return orderId;
    }

    public List<OrderItemResponse> getOrderItems() {
        return orderItems;
    }
}
