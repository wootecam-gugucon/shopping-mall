package com.gugucon.shopping.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "pay.callback")
public class TossPayConfiguration {

    private String successUrl;
    private String failUrl;
}
