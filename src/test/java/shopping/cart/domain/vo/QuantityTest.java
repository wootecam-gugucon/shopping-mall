package shopping.cart.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

@DisplayName("Quantity 단위 테스트")
class QuantityTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 500, 1000})
    @DisplayName("정상 범위의 값(0 ~ 1000)으로 생성할 수 있다.")
    void createSuccess(final int value) {
        /* given */

        /* when & then */
        Assertions.assertThatNoException()
            .isThrownBy(() -> new Quantity(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 1001})
    @DisplayName("범위를 벗어나는 값으로 생성할 수 없다.")
    void createFailure(final int value) {
        /* given */

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
            () -> new Quantity(value));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUANTITY);
    }

    @Test
    @DisplayName("값이 0이다.")
    void isZero() {
        /* given */
        final Quantity zero = new Quantity(0);

        /* when & then */
        assertThat(zero.isZero()).isTrue();
    }
}
