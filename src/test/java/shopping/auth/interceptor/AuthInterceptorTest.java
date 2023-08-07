package shopping.auth.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shopping.auth.utils.JwtProvider;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthInterceptor 단위 테스트")
class AuthInterceptorTest {

    @Mock
    private JwtProvider jwtProvider;
    @InjectMocks
    private AuthInterceptor authInterceptor;

    @Test
    @DisplayName("유효한 토큰인 경우 예외가 발생하지 않는다.")
    void validToken() {
        /* given */
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(jwtProvider.validate("valid_token")).thenReturn(true);
        when(jwtProvider.parseToken("valid_token")).thenReturn("1");
        final ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);

        /* when & then */
        assertThatNoException()
            .isThrownBy(() -> authInterceptor.preHandle(request, null, null));
        verify(request).setAttribute(headerCaptor.capture(), userIdCaptor.capture());
        assertThat(headerCaptor.getValue()).isEqualTo("userId");
        assertThat(userIdCaptor.getValue()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Authorization 헤더 정보가 없으면 예외가 발생한다.")
    void noAuthorizationHeader() {
        /* given */
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
            () -> authInterceptor.preHandle(request, null, null));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_AUTHORIZATION_HEADER);
    }

    @Test
    @DisplayName("지원하는 토큰 타입이 아니면 예외가 발생한다.")
    void invalidTokenType() {
        /* given */
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Pearer ");

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
            () -> authInterceptor.preHandle(request, null, null));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN_TYPE);
    }

    @Test
    @DisplayName("유효한 토큰이 아니면 예외가 발생한다.")
    void invalidToken() {
        /* given */
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");
        when(jwtProvider.validate("invalid_token")).thenReturn(false);

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
            () -> authInterceptor.preHandle(request, null, null));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
    }
}
