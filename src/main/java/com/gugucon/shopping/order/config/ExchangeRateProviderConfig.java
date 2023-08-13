package com.gugucon.shopping.order.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import com.gugucon.shopping.order.service.currency.DefaultExchangeRateProvider;
import com.gugucon.shopping.order.service.currency.ExchangeRateProvider;

@Configuration
@RequiredArgsConstructor
public class ExchangeRateProviderConfig {

    private final RestTemplate restTemplate;

    @Bean
    @Profile("!test")
    public ExchangeRateProvider defaultExchangeRateProvider() {
        return new DefaultExchangeRateProvider(restTemplate);
    }
}
