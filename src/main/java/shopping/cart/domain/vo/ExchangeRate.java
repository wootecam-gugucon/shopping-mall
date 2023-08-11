package shopping.cart.domain.vo;

import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ExchangeRate {

    protected ExchangeRate() {
    }

    private double value;

    public ExchangeRate(final double value) {
        validatePositive(value);
        this.value = value;
    }

    public DollarMoney convert(final WonMoney sourcePrice) {
        validatePositive(value);
        return new DollarMoney(sourcePrice.getValue() / value);
    }

    private void validatePositive(final double value) {
        if (value <= 0) {
            throw new ShoppingException(ErrorCode.INVALID_EXCHANGE_RATE);
        }
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
