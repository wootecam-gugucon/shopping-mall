package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static com.gugucon.shopping.TestUtils.createProduct;
import static com.gugucon.shopping.TestUtils.createSoldOutProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                .quantity(4)
                .build();

        /* when */
        final BigInteger totalPrice = cartItem.getTotalPrice();

        /* then */
        assertThat(totalPrice).isEqualTo(BigInteger.valueOf(40000));
    }

    @Test
    @DisplayName("장바구니 아이템이 품절인지 검사한다.")
    void validateSoldOut() {
        // given
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(1L)
                .product(createProduct("치킨", 10000))
                .quantity(4)
                .build();

        // when & then
        assertThatNoException().isThrownBy(cartItem::validateSoldOut);
    }

    @Test
    @DisplayName("장바구니 아이템이 품절인지 검사할 때 품절이면 예외를 반환한다.")
    void throwException_validateSoldOut() {
        // given
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(1L)
                .product(createSoldOutProduct("치킨", 10000))
                .quantity(4)
                .build();

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, cartItem::validateSoldOut);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOLD_OUT);    }
}
