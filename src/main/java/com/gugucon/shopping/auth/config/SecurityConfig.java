package com.gugucon.shopping.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gugucon.shopping.auth.security.JwtAuthenticationEntryPoint;
import com.gugucon.shopping.auth.security.JwtAuthenticationFilter;
import com.gugucon.shopping.auth.security.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .securityMatchers(security -> security
                .requestMatchers(new AntPathRequestMatcher("/api/v1/**"))
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/api/v1/login"),
                                 new AntPathRequestMatcher("/api/v1/signup"),
                                 new AntPathRequestMatcher("/api/v1/products/**"),
                                 new AntPathRequestMatcher("/api/v1/rate/product/*"),
                                 new AntPathRequestMatcher("/api/v1/rate/product/*/all"))
                .permitAll()
                .anyRequest().authenticated()
            )
            .authenticationProvider(jwtAuthenticationProvider)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint()))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration));
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> register(final JwtAuthenticationFilter authFilter) {
        final FilterRegistrationBean<JwtAuthenticationFilter> registerBean = new FilterRegistrationBean<>(authFilter);
        registerBean.setEnabled(false);
        return registerBean;
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
