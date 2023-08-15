package com.gugucon.shopping.pay.config;

import com.gugucon.shopping.pay.infrastructure.OrderIdBase64Translator;
import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import com.gugucon.shopping.pay.infrastructure.PayValidator;
import com.gugucon.shopping.pay.infrastructure.TossPayValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayConfiguration {

    @Bean
    public OrderIdTranslator orderIdTranslator() {
        return new OrderIdBase64Translator();
    }

    @Bean
    public PayValidator payValidator(@Value("${pay.toss.secret-key}") final String secretKey) {
        return new TossPayValidator(secretKey);
    }
}
