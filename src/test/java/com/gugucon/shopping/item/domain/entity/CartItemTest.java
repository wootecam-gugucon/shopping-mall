package com.gugucon.shopping.item.domain.entity;

import static com.gugucon.shopping.utils.DomainUtils.createProduct;
import static com.gugucon.shopping.utils.DomainUtils.createSoldOutProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CartItem 단위 테스트")
class CartItemTest {

    @Test
    @DisplayName("상품 금액과 주문 수량을 곱한 총 금액을 구한다.")
    void getTotalPrice() {
        /* given */
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(1L)
                .product(createProduct("치킨", 10000))
                .quantity(Quantity.from(4))
                .build();

        /* when */
        final BigInteger totalPrice = cartItem.getTotalPrice();

        /* then */
        assertThat(totalPrice).isEqualTo(BigInteger.valueOf(40000));
    }

    @Test
    @DisplayName("사용자가 장바구니의 사용자인지 검증한다.")
    void validateMember() {
        // given
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(1L)
                .product(createProduct("치킨", 10000))
                .quantity(Quantity.from(4))
                .build();

        // when & then
        assertThatNoException().isThrownBy(() -> cartItem.validateMember(1L));
    }

    @Test
    @DisplayName("사용자가 장바구니의 사용자가 아니면 예외를 반환한다.")
    void validateMember_DifferentMember() {
        // given
        final long memberId = 1L;
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10000))
                .quantity(Quantity.from(4))
                .build();

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> cartItem.validateMember(memberId + 1));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CART_ITEM);
    }

    @Test
    @DisplayName("장바구니 아이템의 수량이 주문 가능한 수량이면 true를 반환한다.")
    void checkAvailableQuantity() {
        // given
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(1L)
                .product(createProduct("치킨", 10000))
                .quantity(Quantity.from(4))
                .build();

        // when
        final boolean result = cartItem.isAvailableQuantity();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("장바구니 아이템의 수량이 주문 가능하지 않은 수량이면 false를 반환한다.")
    void checkAvailableQuantity_tooManyQuantity() {
        // given
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(1L)
                .product(createSoldOutProduct("치킨", 10000))
                .quantity(Quantity.from(4))
                .build();

        // when
        final boolean result = cartItem.isAvailableQuantity();

        // then
        assertThat(result).isFalse();
    }
}
