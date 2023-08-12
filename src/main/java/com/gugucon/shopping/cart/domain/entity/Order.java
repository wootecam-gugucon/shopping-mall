package com.gugucon.shopping.cart.domain.entity;

import com.gugucon.shopping.cart.domain.vo.DollarMoney;
import com.gugucon.shopping.cart.domain.vo.ExchangeRate;
import com.gugucon.shopping.cart.domain.vo.WonMoney;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    private static final long MAX_TOTAL_PRICE = 100_000_000_000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private final List<OrderItem> orderItems = new ArrayList<>();
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    private WonMoney totalPrice;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "exchange_rate"))
    private ExchangeRate exchangeRate;

    protected Order() {
    }

    public Order(final Long id, final Long userId, final ExchangeRate exchangeRate) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = WonMoney.ZERO;
        this.exchangeRate = exchangeRate;
    }

    public static Order from(final Long userId, final List<CartItem> cartItems, final ExchangeRate exchangeRate) {
        Order order = new Order(null, userId, exchangeRate);
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

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public WonMoney getTotalPrice() {
        return totalPrice;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }
}
