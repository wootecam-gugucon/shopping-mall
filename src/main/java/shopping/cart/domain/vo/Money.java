package shopping.cart.domain.vo;

import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class Money {

    public static final Money ZERO = new Money(0);

    private long value;

    protected Money() {
    }

    public Money(final long value) {
        this.value = value;
    }

    public Money add(final Money other) {
        return new Money(value + other.value);
    }

    public Money multiply(final Quantity quantity) {
        return new Money(value * quantity.getValue());
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
        final Money money = (Money) o;
        return value == money.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
