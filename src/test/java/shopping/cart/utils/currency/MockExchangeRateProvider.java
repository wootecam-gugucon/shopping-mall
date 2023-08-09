package shopping.cart.utils.currency;

import shopping.cart.domain.vo.ExchangeRate;

public class MockExchangeRateProvider implements ExchangeRateProvider {

    @Override
    public ExchangeRate fetchExchangeRate() {
        return new ExchangeRate(1319.15);
    }
}
