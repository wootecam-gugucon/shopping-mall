package shopping.cart.utils.currency;

import shopping.cart.domain.MoneyType;
import shopping.cart.domain.vo.ExchangeRate;

//@Component
//@Primary
public class MockExchangeRateProvider implements ExchangeRateProvider {

    @Override
    public ExchangeRate fetchExchangeRateOf(final MoneyType moneyType) {
        return new ExchangeRate(MoneyType.USD, 1319.15);
    }
}
