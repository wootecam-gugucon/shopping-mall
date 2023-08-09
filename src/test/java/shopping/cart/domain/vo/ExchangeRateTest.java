package shopping.cart.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

@DisplayName("ExchangeRate 단위 테스트")
class ExchangeRateTest {

    @Test
    @DisplayName("환율은 양수이다.")
    void createWithPositive() {
        /* given */

        /* when & then */
        assertThatNoException().isThrownBy(() -> new ExchangeRate(1300));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -1})
    @DisplayName("환율은 0 또는 음수일 수 없다.")
    void createWithNonPositive(final double value) {
        /* given */

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
            () -> new ExchangeRate(value));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_EXCHANGE_RATE);
    }
}
