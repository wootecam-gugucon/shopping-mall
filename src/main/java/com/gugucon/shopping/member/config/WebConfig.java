package com.gugucon.shopping.member.config;

import com.gugucon.shopping.member.infrastructure.converter.StringToBirthYearRangeConverter;
import com.gugucon.shopping.member.infrastructure.converter.StringToGenderConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToBirthYearRangeConverter());
        registry.addConverter(new StringToGenderConverter());
    }
}
