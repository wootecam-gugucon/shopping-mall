package shopping.cart.domain.vo;

import java.math.BigDecimal;
import java.util.Objects;
import shopping.cart.domain.MoneyType;

public class ForeignCurrency {

    private final BigDecimal value;
    private final MoneyType moneyType;

    public ForeignCurrency(final BigDecimal value, final MoneyType moneyType) {
        this.value = value;
        this.moneyType = moneyType;
    }

    public BigDecimal getValue() {
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
        return Objects.equals(value, that.value) && moneyType == that.moneyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, moneyType);
    }
}
