package shopping.cart.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    @DisplayName("두 금액을 더한다.")
    void add() {
        /* given */
        final Money money = new Money(10000);
        final Money other = new Money(20000);

        /* when */
        final Money result = money.add(other);

        /* then */
        assertThat(result).isEqualTo(new Money(30000));
    }

    @Test
    @DisplayName("금액을 수량만큼 곱한다.")
    void multiply() {
        /* given */
        final Money money = new Money(10000);
        final Quantity quantity = new Quantity(7);

        /* when */
        final Money result = money.multiply(quantity);

        /* then */
        assertThat(result).isEqualTo(new Money(70000));
    }
}
