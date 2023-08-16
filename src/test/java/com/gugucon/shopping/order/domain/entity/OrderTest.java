package com.gugucon.shopping.order.domain.entity;

import static com.gugucon.shopping.TestUtils.createMember;
import static com.gugucon.shopping.TestUtils.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.CartItem;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Order 단위 테스트")
class OrderTest {

    @Test
    @DisplayName("총 주문 금액을 계산한다.")
    void calculateTotalPrice() {
        /* given */
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10000))
                .quantity(5)
                .build();
        final CartItem cartItem2 = CartItem.builder()
                .id(2L)
                .memberId(memberId)
                .product(createProduct("피자", 20000))
                .quantity(4)
                .build();
        final Order order = Order.from(memberId, List.of(cartItem1, cartItem2));

        /* when & then */
        assertThat(order.calculateTotalPrice()).isEqualTo(WonMoney.from(130000L));
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
                .quantity(1)
                .build();
        final CartItem invalidCartItem = CartItem.builder()
                .id(2L)
                .memberId(memberId)
                .product(createProduct("피자", 100_000_000_001L))
                .quantity(1)
                .build();

        /* when & then */
        assertThatNoException().isThrownBy(() -> Order.validateTotalPrice(List.of(validCartItem)));
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> Order.validateTotalPrice(List.of(invalidCartItem)));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXCEED_MAX_TOTAL_PRICE);
    }

    @Test
    @DisplayName("주어진 ID가 주문자의 ID와 다르면 검증 시 예외가 발생한다.")
    void validateUserHasId() {
        /* given */
        final Long memberId = createMember().getId();
        final Order order = Order.from(memberId, Collections.emptyList());

        /* when & then */
        assertThatNoException().isThrownBy(() -> order.validateUserHasId(memberId));
        ShoppingException exception = assertThrows(ShoppingException.class,
                                                   () -> order.validateUserHasId(Long.MAX_VALUE));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER);
    }
}
