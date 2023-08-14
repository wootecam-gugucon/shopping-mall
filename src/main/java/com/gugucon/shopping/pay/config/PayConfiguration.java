package com.gugucon.shopping.pay.config;

import com.gugucon.shopping.pay.infrastructure.OrderIdBase64Translator;
import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import com.gugucon.shopping.pay.infrastructure.PayValidator;
import com.gugucon.shopping.pay.infrastructure.TossPayValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayConfiguration {

    @Bean
    public OrderIdTranslator orderIdTranslator() {
        return new OrderIdBase64Translator();
    }

    @Bean
    public PayValidator payValidator() {
        return new TossPayValidator();
    }
}
