package com.gugucon.shopping.order.service.currency;

import com.gugucon.shopping.order.domain.vo.ExchangeRate;

public class MockExchangeRateProvider implements ExchangeRateProvider {

    @Override
    public ExchangeRate fetchExchangeRate() {
        return ExchangeRate.from(1319.15);
    }
}
