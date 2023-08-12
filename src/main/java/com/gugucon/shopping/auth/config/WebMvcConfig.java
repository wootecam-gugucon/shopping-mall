package com.gugucon.shopping.auth.config;

import java.util.List;

import com.gugucon.shopping.auth.argumentresolver.UserIdArgumentResolver;
import com.gugucon.shopping.auth.interceptor.AuthInterceptor;
import com.gugucon.shopping.auth.utils.JwtProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtProvider jwtProvider;

    public WebMvcConfig(final JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(jwtProvider))
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/v1/login/**");
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserIdArgumentResolver());
    }
}
