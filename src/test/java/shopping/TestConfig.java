package shopping;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import shopping.cart.utils.currency.ExchangeRateProvider;
import shopping.cart.utils.currency.MockExchangeRateProvider;

@TestConfiguration
public class TestConfig {

    @Bean
    public ExchangeRateProvider mockExchangeRateProvider() {
        return new MockExchangeRateProvider();
    }
}
