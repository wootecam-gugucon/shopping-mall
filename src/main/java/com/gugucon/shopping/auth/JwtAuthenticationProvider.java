package com.gugucon.shopping.auth;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.member.utils.JwtProvider;
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
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwtToken = ((JwtAuthenticationToken) authentication).getJwtToken();
        validateToken(jwtToken);
        String principal = jwtProvider.parseToken(jwtToken);
        return new JwtAuthenticationToken(principal, "", new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void validateToken(String jwtToken) {
        if (!jwtProvider.validate(jwtToken)) {
            throw new BadCredentialsException(ErrorCode.INVALID_TOKEN.getMessage());
        }
    }
}
