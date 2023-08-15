package com.gugucon.shopping.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Github Actions 환경 변수 주입 테스트")
class GitActionSecretTest {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Test
    @DisplayName("secret key 주입 성공")
    void secretKey() {
        // then
        assertThat(jwtSecretKey).isNotBlank();
        assertThat(jwtExpiration).isPositive();
    }
}
