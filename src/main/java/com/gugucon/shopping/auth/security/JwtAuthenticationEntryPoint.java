package com.gugucon.shopping.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gugucon.shopping.common.exception.ErrorResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static com.gugucon.shopping.common.exception.ErrorCode.LOGIN_REQUESTED;

@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {

        final ErrorResponse errorResponse = ErrorResponse.from(LOGIN_REQUESTED);
        final ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(objectMapper.writeValueAsBytes(errorResponse));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(LOGIN_REQUESTED.getStatus().value());
    }
}
