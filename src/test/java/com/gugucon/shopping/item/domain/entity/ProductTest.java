package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.TestUtils;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Product 단위 테스트")
class ProductTest {

    @Test
    @DisplayName("물품이 품절인지 검사한다.")
    void validateSoldOut() {
        // given
        final Product product = TestUtils.createProduct("name", 1000L);

        // when & then
        assertThatNoException().isThrownBy(product::validateSoldOut);
    }

    @Test
    @DisplayName("물품이 품절인지 검사할 때 품절이면 예외를 반환한다.")
    void validateSoldOut_isSoldOut() {
        // given
        final Product product = TestUtils.createSoldOutProduct("name", 1000L);

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, product::validateSoldOut);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOLD_OUT);
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 5})
    @DisplayName("요청한 수량으로 재고를 줄일 수 있으면 true를 반환한다.")
    void canReduceStock(final int quantity) {
        // given
        final Product product = TestUtils.createProduct(5);

        // when
        final boolean result = product.canReduceStockBy(quantity);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("요청한 수량으로 재고를 줄일 수 없으면 false를 반환한다.")
    void canReduceStock_manyQuantity() {
        // given
        final int stock = 5;
        final Product product = TestUtils.createProduct(stock);

        // when
        final boolean result = product.canReduceStockBy(stock + 1);

        // then
        assertThat(result).isFalse();
    }
}
