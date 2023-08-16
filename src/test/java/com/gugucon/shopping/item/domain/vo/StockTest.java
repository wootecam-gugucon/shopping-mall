package com.gugucon.shopping.item.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StockTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
    @DisplayName("재고를 생성한다.")
    void create(final int value) {
        // given

        // when & then
        assertThatNoException().isThrownBy(() -> Stock.from(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, Integer.MAX_VALUE + 1})
    @DisplayName("부적절한 값으로 재고를 생성할 때 예외가 발생한다.")
    void createFail_invalidValue(final int value) {
        // given

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> Stock.from(value));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_STOCK);
    }

    @Test
    @DisplayName("재고가 0일 시 품절 여부를 true로 반환한다.")
    void isSoldOutTrue_valueIsZero() {
        // given
        final Stock stock = Stock.from(0);

        // when
        final boolean result = stock.isSoldOut();

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, Integer.MAX_VALUE})
    @DisplayName("재고가 0이 아닐 시 품절 여부를 false로 반환한다.")
    void isSoldOutTrue_valueIsNotZero(final int value) {
        // given
        final Stock stock = Stock.from(value);

        // when
        final boolean result = stock.isSoldOut();

        // then
        assertThat(result).isFalse();
    }
}
