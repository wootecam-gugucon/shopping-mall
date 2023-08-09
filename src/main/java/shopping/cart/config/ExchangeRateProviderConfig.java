package shopping.cart.config;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import shopping.cart.utils.currency.DefaultExchangeRateProvider;
import shopping.cart.utils.currency.ExchangeRateProvider;

@Configuration
public class ExchangeRateProviderConfig {

    @Bean
    public RestTemplate customRestTemplate() {
        return new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .build();
    }

    @Bean
    @Profile("!test")
    public ExchangeRateProvider defaultExchangeRateProvider() {
        return new DefaultExchangeRateProvider(customRestTemplate());
    }
}
