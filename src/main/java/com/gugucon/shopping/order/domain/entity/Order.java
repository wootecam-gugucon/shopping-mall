package com.gugucon.shopping.order.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.CartItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Order extends BaseTimeEntity {

    private static final long MAX_TOTAL_PRICE = 100_000_000_000L;
    public static final String MULTIPLE_ITEM_EXPRESSION = " 외 %d건";

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private final List<OrderItem> orderItems = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    @NotNull
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderStatus status;

    public static Order from(final Long memberId, final List<CartItem> cartItems) {
        final Order order = new Order(null, memberId, OrderStatus.ORDERED);
        cartItems.stream()
                .map(OrderItem::from)
                .forEach(order::addOrderItem);
        return order;
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

    public Money calculateTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(Money.ZERO, Money::add);
    }

    private void addOrderItem(final OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public String createOrderName() {
        final int size = orderItems.size();
        final OrderItem firstOrderItem = findFirstOrderItem();
        if (hasMultipleOrderItem(size)) {
            return firstOrderItem.getName() + String.format(MULTIPLE_ITEM_EXPRESSION, size-1);
        }
        return firstOrderItem.getName();
    }

    private boolean hasMultipleOrderItem(final int size) {
        return size > 1;
    }

    private OrderItem findFirstOrderItem() {
        return orderItems.stream()
                         .min(Comparator.comparingLong(OrderItem::getId))
                         .orElseThrow(() -> new ShoppingException(ErrorCode.UNKNOWN_ERROR));
    }

    public void validateUnPayed() {
        if (status != OrderStatus.ORDERED) {
            throw new ShoppingException(ErrorCode.PAYED_ORDER);
        }
    }

    public void pay() {
        this.status = OrderStatus.PAYED;
    }

    public enum OrderStatus {ORDERED, PAYED}
}
