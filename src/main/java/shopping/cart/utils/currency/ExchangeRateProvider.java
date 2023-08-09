package shopping.cart.utils.currency;

import shopping.cart.domain.vo.ExchangeRate;

public interface ExchangeRateProvider {

    ExchangeRate fetchExchangeRate();
}
