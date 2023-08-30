package com.gugucon.shopping.order.domain.entity;

import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.order.domain.PayType;
import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static com.gugucon.shopping.utils.DomainUtils.createMember;
import static com.gugucon.shopping.utils.DomainUtils.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Order 단위 테스트")
class OrderTest {

    @Test
    @DisplayName("빈 장바구니로 주문을 생성하는 경우 예외가 발생한다.")
    void createFail_emptyCartItem() {
        // given
        final Long memberId = createMember().getId();

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> Order.from(memberId, Collections.emptyList()));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMPTY_CART);
    }

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
        order.startPay(PayType.POINT);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYING);
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
        order.startPay(PayType.TOSS);

        // when
        order.completePay();

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
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10_000L))
                .quantity(Quantity.from(1))
                .build();
        final Order order = Order.from(memberId, List.of(cartItem));
        order.startPay(PayType.TOSS);

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> order.startPay(PayType.POINT));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_STATUS);
    }

    @Test
    @DisplayName("완료된 주문이면 예외가 발생한다.")
    void validatePending() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10_000L))
                .quantity(Quantity.from(1))
                .build();
        final Order order = Order.from(memberId, List.of(cartItem));
        order.startPay(PayType.TOSS);
        order.completePay();

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, order::completePay);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_STATUS);
    }

    @Test
    @DisplayName("주문 이름을 생성한다.")
    void createOrderName_multipleOrderItems() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10_000L))
                .quantity(Quantity.from(2))
                .build();
        final CartItem cartItem2 = CartItem.builder()
                .id(2L)
                .memberId(memberId)
                .product(createProduct("피자", 20_000L))
                .quantity(Quantity.from(3))
                .build();
        final Order order = Order.from(memberId, List.of(cartItem1, cartItem2));

        // when
        final String orderName = order.createOrderName();

        // then
        assertThat(orderName).isEqualTo("치킨 외 1건");
    }

    @Test
    @DisplayName("주문 이름을 생성한다.")
    void createOrderName_singleOrderItem() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10_000L))
                .quantity(Quantity.from(2))
                .build();
        final Order order = Order.from(memberId, List.of(cartItem));

        // when
        final String orderName = order.createOrderName();

        // then
        assertThat(orderName).isEqualTo("치킨");
    }

    @Test
    @DisplayName("주문 상품이 없는 주문에 대해 주문 이름을 생성하면 예외가 발생한다.")
    void createOrderName_noOrderItem() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10_000L))
                .quantity(Quantity.from(2))
                .build();
        final CartItem cartItem2 = CartItem.builder()
                .id(2L)
                .memberId(memberId)
                .product(createProduct("피자", 20_000L))
                .quantity(Quantity.from(3))
                .build();
        final Order order = Order.from(memberId, List.of(cartItem1, cartItem2));
        order.getOrderItems().clear();

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> order.createOrderName());
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNKNOWN_ERROR);
    }

    @Test
    @DisplayName("주문금액이 같은지 검증한다.")
    void validateMoney() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10_000L))
                .quantity(Quantity.from(2))
                .build();
        final CartItem cartItem2 = CartItem.builder()
                .id(2L)
                .memberId(memberId)
                .product(createProduct("피자", 20_000L))
                .quantity(Quantity.from(3))
                .build();
        final Order order = Order.from(memberId, List.of(cartItem1, cartItem2));
        final Money validMoney = Money.from(80_000L);
        final Money invalidMoney = Money.from(70_000L);

        // when & then
        assertThatNoException().isThrownBy(() -> order.validateMoney(validMoney));
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> order.validateMoney(invalidMoney));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PAY_FAILED);
    }

    @Test
    @DisplayName("주문이 취소되지 않았는지 검증한다.")
    void validateNotCanceled() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10_000L))
                .quantity(Quantity.from(1))
                .build();
        final Order canceledOrder = Order.from(memberId, List.of(cartItem));
        canceledOrder.cancel();
        final Order notCanceledOrder = Order.from(memberId, List.of(cartItem));

        // when & then
        assertThatNoException().isThrownBy(notCanceledOrder::validateNotCanceled);
        final ShoppingException exception = assertThrows(ShoppingException.class, canceledOrder::validateNotCanceled);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNKNOWN_ERROR);
    }

    @Test
    @DisplayName("주문이 생성 상태인지 여부를 반환한다.")
    void isCreated() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10_000L))
                .quantity(Quantity.from(1))
                .build();
        final Order createdOrder = Order.from(memberId, List.of(cartItem));
        final Order canceledOrder = Order.from(memberId, List.of(cartItem));
        canceledOrder.cancel();

        // when & then
        assertThat(createdOrder.isCreated()).isTrue();
        assertThat(canceledOrder.isCreated()).isFalse();
    }

    @Test
    @DisplayName("주문한 회원 ID를 반환한다.")
    void getMemberId() {
        // given
        final Long memberId = createMember().getId();
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10_000L))
                .quantity(Quantity.from(1))
                .build();
        final Order order = Order.from(memberId, List.of(cartItem));

        // when
        final Long memberIdFromOrder = order.getMemberId();

        // then
        assertThat(memberIdFromOrder).isEqualTo(memberId);
    }
}
