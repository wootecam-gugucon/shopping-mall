package com.gugucon.shopping.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import com.gugucon.shopping.order.service.currency.DefaultExchangeRateProvider;
import com.gugucon.shopping.order.service.currency.ExchangeRateProvider;

@Configuration
public class ExchangeRateProviderConfig {

    private final RestTemplate restTemplate;

    public ExchangeRateProviderConfig(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Bean
    @Profile("!test")
    public ExchangeRateProvider defaultExchangeRateProvider() {
        return new DefaultExchangeRateProvider(restTemplate);
    }
}
