package com.gugucon.shopping.member.infrastructure.converter;


import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.member.domain.vo.Gender;
import org.springframework.core.convert.converter.Converter;

public class StringToGenderConverter implements Converter<String, Gender> {
    @Override
    public Gender convert(String source) {
        try {
            return Gender.from(source);
        } catch (IllegalArgumentException e) {
            throw new ShoppingException(ErrorCode.INVALID_GENDER);
        }
    }
}
