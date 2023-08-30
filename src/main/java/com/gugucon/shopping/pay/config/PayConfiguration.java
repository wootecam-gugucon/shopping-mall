package com.gugucon.shopping.pay.config;

import com.gugucon.shopping.pay.infrastructure.OrderIdBase64Translator;
import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import com.gugucon.shopping.pay.infrastructure.PayValidator;
import com.gugucon.shopping.pay.infrastructure.TossPayValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class PayConfiguration {

    private final RestTemplate restTemplate;

    @Bean
    public OrderIdTranslator orderIdTranslator() {
        return new OrderIdBase64Translator();
    }

    @Bean
    public PayValidator payValidator(@Value("${pay.toss.secret-key}") final String secretKey) {
        return new TossPayValidator(restTemplate, secretKey);
    }
}
