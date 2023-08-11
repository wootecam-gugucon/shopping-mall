package shopping;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import shopping.cart.service.currency.ExchangeRateProvider;
import shopping.cart.service.currency.MockExchangeRateProvider;

@TestConfiguration
public class TestConfig {

    @Bean
    public ExchangeRateProvider mockExchangeRateProvider() {
        return new MockExchangeRateProvider();
    }
}
