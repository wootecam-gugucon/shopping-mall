package com.gugucon.shopping.member.interceptor;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.member.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_TOKEN_TYPE = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String MEMBER_ID = "memberId";

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) {

        final String header = request.getHeader(AUTHORIZATION);
        validateAuthorizationHeader(header);
        validateTokenType(header);

        String token = header.substring(BEARER_TOKEN_TYPE.length());
        validateToken(token);

        Long memberId = Long.valueOf(jwtProvider.parseToken(token));
        request.setAttribute(MEMBER_ID, memberId);
        return true;
    }

    private void validateToken(final String token) {
        if (!jwtProvider.validate(token)) {
            throw new ShoppingException(ErrorCode.INVALID_TOKEN);
        }
    }

    private void validateAuthorizationHeader(final String header) {
        if (Objects.isNull(header)) {
            throw new ShoppingException(ErrorCode.NO_AUTHORIZATION_HEADER);
        }
    }

    private void validateTokenType(final String header) {
        if (!header.startsWith(BEARER_TOKEN_TYPE)) {
            throw new ShoppingException(ErrorCode.INVALID_TOKEN_TYPE);
        }
    }
}
