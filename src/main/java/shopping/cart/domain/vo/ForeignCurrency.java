package shopping.cart.domain.vo;

import java.util.Objects;
import shopping.cart.domain.MoneyType;

public class ForeignCurrency {

    private final long value;
    private final MoneyType moneyType;

    public ForeignCurrency(final long value, final MoneyType moneyType) {
        this.value = value;
        this.moneyType = moneyType;
    }

    public long getValue() {
        return value;
    }

    public MoneyType getMoneyType() {
        return moneyType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ForeignCurrency that = (ForeignCurrency) o;
        return value == that.value && moneyType == that.moneyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, moneyType);
    }
}
