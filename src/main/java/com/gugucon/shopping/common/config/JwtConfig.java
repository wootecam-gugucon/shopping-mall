package com.gugucon.shopping.common.config;

import com.gugucon.shopping.common.utils.JwtProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private final String secretKey;
    private final Long expiration;

    public JwtConfig(final String secretKey, final Long expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider(secretKey, expiration);
    }
}
