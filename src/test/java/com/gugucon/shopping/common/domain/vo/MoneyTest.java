package com.gugucon.shopping.common.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Money 단위 테스트")
class MoneyTest {

    @DisplayName("금액은 0보다 작을 수 없다")
    @ParameterizedTest
    @ValueSource(longs = {-1})
    void validate(long value) {
        // when
        final Exception exception = catchException(() -> Money.from(value));

        // then
        assertThat(exception).isInstanceOf(ShoppingException.class);
    }

    @Test
    @DisplayName("두 금액을 더한다.")
    void add() {
        /* given */
        final Money money = Money.from(10000L);
        final Money other = Money.from(20000L);

        /* when */
        final Money result = money.add(other);

        /* then */
        assertThat(result).isEqualTo(Money.from(30000L));
    }

    @Test
    @DisplayName("금액을 수량만큼 곱한다.")
    void multiply() {
        /* given */
        final Money money = Money.from(10000L);
        final Quantity quantity = Quantity.from(7);

        /* when */
        final Money result = money.multiply(quantity);

        /* then */
        assertThat(result).isEqualTo(Money.from(70000L));
    }
}
