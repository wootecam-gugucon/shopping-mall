package com.gugucon.shopping.order.domain.entity;

import static com.gugucon.shopping.utils.DomainUtils.createMember;
import static com.gugucon.shopping.utils.DomainUtils.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.order.domain.PayType;
import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Order 단위 테스트")
class OrderTest {

    @Test
    @DisplayName("생성되면 CREATED 상태가 된다")
    void status_created() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = CartItem.builder()
                                           .id(1L)
                                           .memberId(memberId)
                                           .product(createProduct("치킨", 10000))
                                           .quantity(Quantity.from(5))
                                           .build();
        final CartItem cartItem2 = CartItem.builder()
                                           .id(2L)
                                           .memberId(memberId)
                                           .product(createProduct("피자", 20000))
                                           .quantity(Quantity.from(4))
                                           .build();

        // when
        final Order order = Order.from(memberId, List.of(cartItem1, cartItem2));

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getPayType()).isEqualTo(PayType.NONE);
    }

    @Test
    @DisplayName("결제 요청을 보내면 PENDING 상태가 된다")
    void status_pending() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = CartItem.builder()
                                           .id(1L)
                                           .memberId(memberId)
                                           .product(createProduct("치킨", 10000))
                                           .quantity(Quantity.from(5))
                                           .build();
        final CartItem cartItem2 = CartItem.builder()
                                           .id(2L)
                                           .memberId(memberId)
                                           .product(createProduct("피자", 20000))
                                           .quantity(Quantity.from(4))
                                           .build();

        final Order order = Order.from(memberId, List.of(cartItem1, cartItem2));

        // when
        order.order(PayType.POINT);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("결제가 완료되면 COMPLETED 상태가 된다")
    void status_completed() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = CartItem.builder()
                                           .id(1L)
                                           .memberId(memberId)
                                           .product(createProduct("치킨", 10000))
                                           .quantity(Quantity.from(5))
                                           .build();
        final CartItem cartItem2 = CartItem.builder()
                                           .id(2L)
                                           .memberId(memberId)
                                           .product(createProduct("피자", 20000))
                                           .quantity(Quantity.from(4))
                                           .build();

        final Order order = Order.from(memberId, List.of(cartItem1, cartItem2));
        order.order(PayType.TOSS);

        // when
        order.pay();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("총 주문 금액을 계산한다.")
    void calculateTotalPrice() {
        /* given */
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10000))
                .quantity(Quantity.from(5))
                .build();
        final CartItem cartItem2 = CartItem.builder()
                .id(2L)
                .memberId(memberId)
                .product(createProduct("피자", 20000))
                .quantity(Quantity.from(4))
                .build();
        final Order order = Order.from(memberId, List.of(cartItem1, cartItem2));

        /* when & then */
        assertThat(order.calculateTotalPrice()).isEqualTo(Money.from(130000L));
    }

    @Test
    @DisplayName("주문 총액이 100_000_000_000원을 넘으면 검증 시 예외가 발생한다.")
    void validateTotalPriceFail_outOfBound() {
        /* given */
        final Long memberId = createMember().getId();
        final CartItem validCartItem = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 100_000_000_000L))
                .quantity(Quantity.from(1))
                .build();
        final CartItem invalidCartItem = CartItem.builder()
                .id(2L)
                .memberId(memberId)
                .product(createProduct("피자", 100_000_000_001L))
                .quantity(Quantity.from(1))
                .build();

        /* when & then */
        assertThatNoException().isThrownBy(() -> Order.validateTotalPrice(List.of(validCartItem)));
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> Order.validateTotalPrice(List.of(invalidCartItem)));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXCEED_MAX_TOTAL_PRICE);
    }

    @Test
    @DisplayName("대기중인 주문이면 예외가 발생한다.")
    void validateCreated() {
        // given
        final Long memberId = createMember().getId();
        final Order order = Order.from(memberId, Collections.emptyList());
        order.order(PayType.TOSS);

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> order.order(PayType.POINT));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_STATUS);
    }

    @Test
    @DisplayName("완료된 주문이면 예외가 발생한다.")
    void validatePending() {
        // given
        final Long memberId = createMember().getId();
        final Order order = Order.from(memberId, Collections.emptyList());
        order.order(PayType.TOSS);
        order.pay();

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, order::pay);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_STATUS);
    }
}
