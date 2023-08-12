package com.gugucon.shopping.order.service.currency;

import com.gugucon.shopping.order.domain.vo.ExchangeRate;

public interface ExchangeRateProvider {

    ExchangeRate fetchExchangeRate();
}
