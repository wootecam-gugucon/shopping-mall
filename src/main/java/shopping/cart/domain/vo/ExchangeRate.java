package shopping.cart.domain.vo;

import java.util.Objects;
import shopping.cart.domain.MoneyType;

public class ExchangeRate {

    private final MoneyType moneyType;
    private final double ratio;

    public ExchangeRate(final MoneyType moneyType, final double ratio) {
        this.moneyType = moneyType;
        this.ratio = ratio;
    }

    public MoneyType getMoneyType() {
        return moneyType;
    }

    public double getRatio() {
        return ratio;
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
        return Double.compare(that.ratio, ratio) == 0 && moneyType == that.moneyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moneyType, ratio);
    }
}
