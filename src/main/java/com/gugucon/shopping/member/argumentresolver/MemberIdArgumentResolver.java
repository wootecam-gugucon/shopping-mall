package com.gugucon.shopping.member.argumentresolver;

import java.util.Objects;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.gugucon.shopping.member.argumentresolver.annotation.MemberId;

public class MemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String MEMBER_ID = "memberId";

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MemberId.class) && Objects.equals(
            parameter.getParameterType(), Long.class);
    }

    @Override
    public Long resolveArgument(final MethodParameter parameter,
        final ModelAndViewContainer mavContainer,
        final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        return (Long) request.getAttribute(MEMBER_ID);
    }
}
