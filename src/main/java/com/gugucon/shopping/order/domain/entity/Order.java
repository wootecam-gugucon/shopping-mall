package com.gugucon.shopping.order.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.CartItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Order extends BaseTimeEntity {

    private static final long MAX_TOTAL_PRICE = 100_000_000_000L;

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

    public WonMoney calculateTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(WonMoney.ZERO, WonMoney::add);
    }

    private void addOrderItem(final OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public String getOrderName() {
        final int size = orderItems.size();
        final OrderItem firstOrderItem = orderItems.stream()
                .min(Comparator.comparingLong(OrderItem::getId))
                .orElseThrow(() -> new ShoppingException(ErrorCode.UNKNOWN_ERROR));
        if (size >= 2) {
            return firstOrderItem.getName() + " 외 " + (size - 1) + "건";
        }
        return firstOrderItem.getName();
    }

    public void validateMemberHasId(Long memberId) {
        if (!Objects.equals(this.memberId, memberId)) {
            throw new ShoppingException(ErrorCode.INVALID_ORDER);
        }
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
