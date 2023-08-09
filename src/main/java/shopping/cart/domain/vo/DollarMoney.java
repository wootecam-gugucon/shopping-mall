package shopping.cart.domain.vo;

import java.math.BigDecimal;
import java.util.Objects;

public class DollarMoney {

    private final BigDecimal value;

    public DollarMoney(final BigDecimal bigDecimal) {
        this.value = bigDecimal;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DollarMoney dollarMoney = (DollarMoney) o;
        return Objects.equals(value, dollarMoney.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
