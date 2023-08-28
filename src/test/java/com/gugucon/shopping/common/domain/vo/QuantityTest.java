package com.gugucon.shopping.common.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Quantity 단위 테스트")
class QuantityTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 500, 1000})
    @DisplayName("수량을 생성한다.")
    void create(final int value) {
        /* given */

        /* when & then */
        Assertions.assertThatNoException()
                .isThrownBy(() -> Quantity.from(value));
    }

    @Test
    @DisplayName("값이 음수이면 수량을 생성했을 때 예외가 발생한다.")
    void createFail_negativeValue() {
        /* given */

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> Quantity.from(-1));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUANTITY);
    }

    @Test
    @DisplayName("값이 0이다.")
    void isZero() {
        /* given */
        final Quantity zero = Quantity.from(0);

        /* when & then */
        assertThat(zero.isZero()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, Integer.MAX_VALUE})
    @DisplayName("수량이 0이 아닐 시 false를 반환한다.")
    void isZero_valueIsNotZero(final int value) {
        // given
        final Quantity quantity = Quantity.from(value);

        // when
        final boolean result = quantity.isZero();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("주어진 수량보다 적으면 true를 반환한다.")
    void isLessThan() {
        // given
        final Quantity quantity = Quantity.from(3);

        // when
        final boolean result = quantity.isLessThan(Quantity.from(4));

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 5})
    @DisplayName("주어진 수량보다 적지 않으면 false를 반환한다.")
    void isLessThan_manyQuantity(final int value) {
        // given
        final Quantity quantity = Quantity.from(5);

        // when
        final boolean result = quantity.isLessThan(Quantity.from(value));

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("수량을 더한다.")
    void increaseBy() {
        // given
        final Quantity five = Quantity.from(5);
        final Quantity three = Quantity.from(3);
        final Quantity eight = Quantity.from(8);

        // when & then
        assertThat(five.increaseBy(three)).isEqualTo(eight);
    }

    @Test
    @DisplayName("수량을 뺀다.")
    void decreaseBy() {
        // given
        final Quantity eight = Quantity.from(8);
        final Quantity five = Quantity.from(5);
        final Quantity three = Quantity.from(3);

        // when & then
        assertThat(eight.decreaseBy(five)).isEqualTo(three);
    }
}
