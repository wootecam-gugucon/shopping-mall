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

    @ParameterizedTest
    @ValueSource(ints = {-1, 1001})
    @DisplayName("값이 정상 범위를 벗어나면 수량을 생성할 수 없다.")
    void createFail_rangeOutOfBound(final int value) {
        /* given */

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
                () -> Quantity.from(value));
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
}
