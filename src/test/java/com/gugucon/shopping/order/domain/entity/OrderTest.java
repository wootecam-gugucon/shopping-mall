package com.gugucon.shopping.order.domain.entity;

import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.order.domain.vo.ExchangeRate;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static com.gugucon.shopping.TestUtils.createProduct;
import static com.gugucon.shopping.TestUtils.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Order 단위 테스트")
class OrderTest {

    @Test
    @DisplayName("총 주문 금액을 달러로 환산해서 반환한다.")
    void getTotalPriceInDollar() {
        /* given */
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = new CartItem(1L, memberId, createProduct("치킨", 10000), 5);
        final CartItem cartItem2 = new CartItem(2L, memberId, createProduct("피자", 20000), 4);
        final Order order = Order.from(memberId, List.of(cartItem1, cartItem2), new ExchangeRate(1300));

        /* when & then */
        assertThat(order.getTotalPriceInDollar().getValue()).isEqualTo(100);
    }

    @Test
    @DisplayName("주문 총액은 100_000_000_000원을 넘을 수 없다.")
    void validateTotalPrice() {
        /* given */
        final Long memberId = createMember().getId();
        final CartItem validCartItem = new CartItem(1L, memberId,
                createProduct("치킨", 100_000_000_000L), 1);
        final CartItem invalidCartItem = new CartItem(2L, memberId,
                createProduct("피자", 100_000_000_001L), 1);

        /* when & then */
        assertThatNoException()
                .isThrownBy(() -> Order.validateTotalPrice(List.of(validCartItem)));
        final ShoppingException exception = assertThrows(ShoppingException.class,
                () -> Order.validateTotalPrice(List.of(invalidCartItem)));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXCEED_MAX_TOTAL_PRICE);
    }

    @Test
    @DisplayName("주문자의 ID를 검증한다.")
    void validateUserHasId() {
        /* given */
        final Long memberId = createMember().getId();
        final Order order = Order.from(memberId, Collections.emptyList(), new ExchangeRate(1300));

        /* when & then */
        assertThatNoException().isThrownBy(() -> order.validateUserHasId(memberId));
        ShoppingException exception = assertThrows(ShoppingException.class, () -> order.validateUserHasId(Long.MAX_VALUE));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER);
    }
}
