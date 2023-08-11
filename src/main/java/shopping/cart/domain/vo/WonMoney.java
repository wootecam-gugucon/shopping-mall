package shopping.cart.domain.vo;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class WonMoney {

    public static final WonMoney ZERO = new WonMoney(0);

    private long value;

    protected WonMoney() {
    }

    public WonMoney(final long value) {
        this.value = value;
    }

    public WonMoney add(final WonMoney other) {
        return new WonMoney(value + other.value);
    }

    public WonMoney multiply(final Quantity quantity) {
        return new WonMoney(value * quantity.getValue());
    }

    public long getValue() {
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
        final WonMoney money = (WonMoney) o;
        return value == money.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
