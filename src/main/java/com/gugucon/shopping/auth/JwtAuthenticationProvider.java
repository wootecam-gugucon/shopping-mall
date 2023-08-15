package com.gugucon.shopping.auth;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.utils.JwtProvider;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String jwtToken = ((JwtAuthenticationToken) authentication).getJwtToken();
        validateToken(jwtToken);
        final Long principal = Long.valueOf(jwtProvider.parseToken(jwtToken));
        return new JwtAuthenticationToken(principal, "", new ArrayList<>());
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void validateToken(final String jwtToken) {
        if (!jwtProvider.validate(jwtToken)) {
            throw new BadCredentialsException(ErrorCode.INVALID_TOKEN.getMessage());
        }
    }
}
