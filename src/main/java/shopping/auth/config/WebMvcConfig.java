package shopping.auth.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import shopping.auth.argumentresolver.UserIdArgumentResolver;
import shopping.auth.interceptor.AuthInterceptor;
import shopping.auth.utils.JwtProvider;

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
