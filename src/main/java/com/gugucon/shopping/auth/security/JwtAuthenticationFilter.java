package com.gugucon.shopping.auth.security;

import com.gugucon.shopping.auth.domain.vo.JwtAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_TOKEN_TYPE = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        final String token = resolveJwtToken(request);

        if (StringUtils.hasText(token)) {
            try {
                final Authentication jwtAuthenticationToken = new JwtAuthenticationToken(token);
                final Authentication authentication = authenticationManager.authenticate(jwtAuthenticationToken);

                final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);
            } catch (AuthenticationException exception) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveJwtToken(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(AUTHORIZATION);
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_TYPE)) {
            return bearerToken.substring(BEARER_TOKEN_TYPE.length());
        }
        return "";
    }
}
