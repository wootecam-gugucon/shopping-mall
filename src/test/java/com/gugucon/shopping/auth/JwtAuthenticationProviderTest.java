package com.gugucon.shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.utils.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@DisplayName("JwtAuthenticationProvider 단위 테스트")
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationProviderTest {

    @InjectMocks
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("JwtAuthenticationToken 클래스를 지원한다")
    void supports() {
        // when
        final boolean result = jwtAuthenticationProvider.supports(JwtAuthenticationToken.class);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("JwtAuthenticationToken 클래스의 하위 클래스가 아니면 지원하지 않는다")
    void supportsFail_NotSubTypeClass() {
        // when
        final boolean result = jwtAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("JwtAuthenticationToken 인증에 성공한다")
    void authenticate() {
        // given
        final String jwtToken = "validJwtToken";
        final String principal = "12";
        final JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwtToken);

        when(jwtProvider.validate(jwtToken)).thenReturn(true);
        when(jwtProvider.parseToken(jwtToken)).thenReturn(principal);

        // when
        Authentication result = jwtAuthenticationProvider.authenticate(authenticationToken);

        // then
        assertThat(result).isInstanceOf(JwtAuthenticationToken.class);
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getPrincipal()).isEqualTo(Long.valueOf(principal));
    }

    @Test
    @DisplayName("유효한 jwt 를 포함한 인증 토큰이 아니면 예외가 발생한다")
    void authenticateFail_invalidJwtAuthenticationToken() {
        // given
        final String jwtToken = "validJwtToken";
        final JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwtToken);

        when(jwtProvider.validate(jwtToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> jwtAuthenticationProvider.authenticate(authenticationToken))
            .hasMessage(ErrorCode.INVALID_TOKEN.getMessage())
            .isInstanceOf(BadCredentialsException.class);
    }
}