package com.gugucon.shopping.common.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WonMoney 단위 테스트")
class WonMoneyTest {

    @Test
    @DisplayName("두 금액을 더한다.")
    void add() {
        /* given */
        final WonMoney wonMoney = WonMoney.from(10000L);
        final WonMoney other = WonMoney.from(20000L);

        /* when */
        final WonMoney result = wonMoney.add(other);

        /* then */
        assertThat(result).isEqualTo(WonMoney.from(30000L));
    }

    @Test
    @DisplayName("금액을 수량만큼 곱한다.")
    void multiply() {
        /* given */
        final WonMoney wonMoney = WonMoney.from(10000L);
        final Quantity quantity = Quantity.from(7);

        /* when */
        final WonMoney result = wonMoney.multiply(quantity);

        /* then */
        assertThat(result).isEqualTo(WonMoney.from(70000L));
    }
}
