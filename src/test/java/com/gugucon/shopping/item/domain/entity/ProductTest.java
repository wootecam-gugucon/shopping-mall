package com.gugucon.shopping.item.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.utils.DomainUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Product 단위 테스트")
class ProductTest {

    @Test
    @DisplayName("물품이 품절인지 검사한다.")
    void validateSoldOut() {
        // given
        final Product product = DomainUtils.createProduct("name", 1000L);

        // when & then
        assertThatNoException().isThrownBy(product::validateSoldOut);
    }

    @Test
    @DisplayName("물품이 품절인지 검사할 때 품절이면 예외를 반환한다.")
    void validateSoldOut_isSoldOut() {
        // given
        final Product product = DomainUtils.createSoldOutProduct("name", 1000L);

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, product::validateSoldOut);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOLD_OUT);
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 5})
    @DisplayName("요청한 수량으로 재고를 줄일 수 있으면 true를 반환한다.")
    void canReduceStock(final int value) {
        // given
        final Product product = DomainUtils.createProduct(5);

        // when
        final boolean result = product.canReduceStockBy(Quantity.from(value));

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("요청한 수량으로 재고를 줄일 수 없으면 false를 반환한다.")
    void canReduceStock_manyQuantity() {
        // given
        final int stock = 5;
        final Product product = DomainUtils.createProduct(stock);

        // when
        final boolean result = product.canReduceStockBy(Quantity.from(stock + 1));

        // then
        assertThat(result).isFalse();
    }
}
