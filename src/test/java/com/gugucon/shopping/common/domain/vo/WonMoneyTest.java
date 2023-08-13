package com.gugucon.shopping.common.domain.vo;

import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WonMoneyTest {

    @Test
    @DisplayName("두 금액을 더한다.")
    void add() {
        /* given */
        final WonMoney wonMoney = WonMoney.from(10000);
        final WonMoney other = WonMoney.from(20000);

        /* when */
        final WonMoney result = wonMoney.add(other);

        /* then */
        assertThat(result).isEqualTo(WonMoney.from(30000));
    }

    @Test
    @DisplayName("금액을 수량만큼 곱한다.")
    void multiply() {
        /* given */
        final WonMoney wonMoney = WonMoney.from(10000);
        final Quantity quantity = Quantity.from(7);

        /* when */
        final WonMoney result = wonMoney.multiply(quantity);

        /* then */
        assertThat(result).isEqualTo(WonMoney.from(70000));
    }
}
