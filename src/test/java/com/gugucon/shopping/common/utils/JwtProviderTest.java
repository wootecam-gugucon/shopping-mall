package com.gugucon.shopping.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private final JwtProvider jwtProvider = new JwtProvider("secretKey", 100_000_000L);

    @Test
    @DisplayName("파싱 불가능한 토큰을 입력하면 검증 시 false를 반환한다.")
    void validate_cannotParseToken() {
        // given

        // when
        final boolean isValid = jwtProvider.validate("invalid_token");

        // then
        assertThat(isValid).isFalse();
    }
}
