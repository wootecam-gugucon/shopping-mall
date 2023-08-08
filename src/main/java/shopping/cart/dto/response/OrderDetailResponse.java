package shopping.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import shopping.cart.domain.entity.Order;
import shopping.cart.domain.vo.ExchangeRate;
import shopping.cart.domain.vo.ForeignCurrency;

public final class OrderDetailResponse {

    private Long orderId;
    private List<OrderItemResponse> orderItems;
    private long totalPrice;
    private BigDecimal foreignCurrencyTotalPrice;
    private double exchangeRate;
    private String unit;

    private OrderDetailResponse() {
    }

    public OrderDetailResponse(final Long orderId, final List<OrderItemResponse> orderItems,
        final long totalPrice,
        final BigDecimal foreignCurrencyTotalPrice, final double exchangeRate, final String unit) {
        this.orderId = orderId;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        this.foreignCurrencyTotalPrice = foreignCurrencyTotalPrice;
        this.exchangeRate = exchangeRate;
        this.unit = unit;
    }

    public static OrderDetailResponse from(final Order order,
        final ForeignCurrency foreignCurrencyTotalPrice,
        final ExchangeRate exchangeRate) {
        final List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
            .map(OrderItemResponse::from)
            .collect(Collectors.toUnmodifiableList());
        return new OrderDetailResponse(order.getId(), orderItemResponses,
            order.getTotalPrice().getValue(), foreignCurrencyTotalPrice.getValue(),
            exchangeRate.getRatio(), foreignCurrencyTotalPrice.getMoneyType().getUnit());
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

    public BigDecimal getForeignCurrencyTotalPrice() {
        return foreignCurrencyTotalPrice;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public String getUnit() {
        return unit;
    }
}
