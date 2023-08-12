package com.gugucon.shopping.order.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.order.domain.vo.DollarMoney;
import com.gugucon.shopping.order.domain.vo.ExchangeRate;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.CartItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseTimeEntity {

    public enum OrderStatus { ORDERED, PAYED, DELIVERED }

    private static final long MAX_TOTAL_PRICE = 100_000_000_000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private final List<OrderItem> orderItems = new ArrayList<>();
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    private WonMoney totalPrice;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "exchange_rate"))
    private ExchangeRate exchangeRate;

    public Order(Long id, Long userId, OrderStatus status, ExchangeRate exchangeRate) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.totalPrice = WonMoney.ZERO;
        this.exchangeRate = exchangeRate;
    }

    public static Order from(final Long userId, final List<CartItem> cartItems, final ExchangeRate exchangeRate) {
        Order order = new Order(null, userId, OrderStatus.ORDERED, exchangeRate);
        cartItems.stream()
                .map(OrderItem::from)
                .forEach(order::addOrderItem);
        return order;
    }

    private void addOrderItem(final OrderItem orderItem) {
        orderItems.add(orderItem);
        totalPrice = totalPrice.add(orderItem.getTotalPrice());
    }

    public DollarMoney getTotalPriceInDollar() {
        return exchangeRate.convert(totalPrice);
    }

    public static void validateTotalPrice(final List<CartItem> cartItems) {
        final BigInteger totalPrice = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigInteger.ZERO, BigInteger::add);
        validateRangeOf(totalPrice);
    }

    private static void validateRangeOf(final BigInteger totalPrice) {
        if (totalPrice.compareTo(BigInteger.valueOf(MAX_TOTAL_PRICE)) > 0) {
            throw new ShoppingException(ErrorCode.EXCEED_MAX_TOTAL_PRICE);
        }
    }

    public void validateUserHasId(Long userId) {
        if (!Objects.equals(this.userId, userId)) {
            throw new ShoppingException(ErrorCode.INVALID_ORDER);
        }
    }
}
