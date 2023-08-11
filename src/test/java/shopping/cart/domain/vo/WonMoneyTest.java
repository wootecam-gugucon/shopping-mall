package shopping.cart.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WonMoneyTest {

    @Test
    @DisplayName("두 금액을 더한다.")
    void add() {
        /* given */
        final WonMoney wonMoney = new WonMoney(10000);
        final WonMoney other = new WonMoney(20000);

        /* when */
        final WonMoney result = wonMoney.add(other);

        /* then */
        assertThat(result).isEqualTo(new WonMoney(30000));
    }

    @Test
    @DisplayName("금액을 수량만큼 곱한다.")
    void multiply() {
        /* given */
        final WonMoney wonMoney = new WonMoney(10000);
        final Quantity quantity = new Quantity(7);

        /* when */
        final WonMoney result = wonMoney.multiply(quantity);

        /* then */
        assertThat(result).isEqualTo(new WonMoney(70000));
    }
}
