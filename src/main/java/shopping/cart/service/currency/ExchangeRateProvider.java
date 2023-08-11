package shopping.cart.service.currency;

import shopping.cart.domain.vo.ExchangeRate;

public interface ExchangeRateProvider {

    ExchangeRate fetchExchangeRate();
}
