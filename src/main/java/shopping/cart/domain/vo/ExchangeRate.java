package shopping.cart.domain.vo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class ExchangeRate {

    protected ExchangeRate() {
    }

    private double value;

    public ExchangeRate(final double value) {
        this.value = value;
    }

    public DollarMoney convert(final Money sourcePrice) {
        final BigDecimal sourceValue = BigDecimal.valueOf(sourcePrice.getValue());
        final BigDecimal targetValue = sourceValue.divide(BigDecimal.valueOf(value),
            MathContext.DECIMAL128);
        return new DollarMoney(targetValue);
    }

    public double getValue() {
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
        final ExchangeRate that = (ExchangeRate) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
