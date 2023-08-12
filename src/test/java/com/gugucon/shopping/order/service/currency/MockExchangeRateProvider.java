package com.gugucon.shopping.order.service.currency;

import com.gugucon.shopping.order.domain.vo.ExchangeRate;
import com.gugucon.shopping.order.service.currency.ExchangeRateProvider;

public class MockExchangeRateProvider implements ExchangeRateProvider {

    @Override
    public ExchangeRate fetchExchangeRate() {
        return new ExchangeRate(1319.15);
    }
}
