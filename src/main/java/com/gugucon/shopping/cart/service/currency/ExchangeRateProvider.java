package com.gugucon.shopping.cart.service.currency;

import com.gugucon.shopping.cart.domain.vo.ExchangeRate;

public interface ExchangeRateProvider {

    ExchangeRate fetchExchangeRate();
}
