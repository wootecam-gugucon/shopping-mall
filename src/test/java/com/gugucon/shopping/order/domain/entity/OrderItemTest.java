package com.gugucon.shopping.order.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.utils.DomainUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OrderItem 단위 테스트")
class OrderItemTest {

    @Test
    @DisplayName("OrderItem을 생성한다.")
    void create() {
        // given
        final CartItem cartItem = DomainUtils.createCartItem();

        // when & then
        assertThatNoException().isThrownBy(() -> OrderItem.from(cartItem));
    }

    @Test
    @DisplayName("품절된 상품을 포함해 OrderItem을 생성하면 예외를 반환한다.")
    void createFail_soldOutProduct() {
        // given
        final CartItem cartItem = CartItem.builder()
                .product(DomainUtils.createSoldOutProduct("name", 1000))
                .quantity(1)
                .memberId(1L)
                .build();

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> OrderItem.from(cartItem));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOLD_OUT);
    }

    @Test
    @DisplayName("재고보다 많은 수량으로 OrderItem을 생성하면 예외를 반환한다.")
    void createFail_tooManyQuantity() {
        // given
        final CartItem cartItem = CartItem.builder()
                .product(DomainUtils.createProduct(1))
                .quantity(2)
                .memberId(1L)
                .build();

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> OrderItem.from(cartItem));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LACK_OF_STOCK);
    }
}
