package com.gugucon.shopping.common.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Money 단위 테스트")
class MoneyTest {

    @DisplayName("Money 를 생성한다")
    void create() {
        // given
        Long value = 10L;

        // when
        Money money = Money.from(value);

        // then
        assertThat(money.getValue()).isEqualTo(value);
    }

    @DisplayName("금액은 0보다 작을 수 없다")
    @ParameterizedTest
    @ValueSource(longs = {-1})
    void createFail_negativeValue(long value) {
        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> Money.from(value));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_MONEY);
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
    @DisplayName("두 금액을 뺀다.")
    void subtract() {
        /* given */
        final Money money = Money.from(10000L);
        final Money other = Money.from(5000L);

        /* when */
        final Money result = money.subtract(other);

        /* then */
        assertThat(result).isEqualTo(Money.from(5000L));
    }

    @Test
    @DisplayName("두 금액을 뺄 때 빼는 금액이 더 크면 예외를 던진다.")
    void subtractFail_BiggerSubstitution() {
        /* given */
        final Money money = Money.from(10000L);
        final Money other = Money.from(20000L);

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> money.subtract(other));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_MONEY);
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

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    @DisplayName("금액이 0 이하인지 확인한다.")
    void isNotPositive(long price) {
        // given
        final Money money = Money.from(price);

        // when & then
        assertThat(money.isNotPositive()).isEqualTo(price <= 0);
    }

    @Test
    @DisplayName("금액이 다른 금액보다 작은지 확인한다.")
    void isLessThan() {
        // given
        final Money money = Money.from(1000L);
        final Money bigger = Money.from(5000L);
        final Money lesser = Money.from(500L);

        // when & then
        assertThat(money.isLessThan(bigger)).isTrue();
        assertThat(money.isLessThan(lesser)).isFalse();
    }
}
