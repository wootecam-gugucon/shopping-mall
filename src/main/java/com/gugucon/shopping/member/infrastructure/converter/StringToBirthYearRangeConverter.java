package com.gugucon.shopping.member.infrastructure.converter;


import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import org.springframework.core.convert.converter.Converter;

public class StringToBirthYearRangeConverter implements Converter<String, BirthYearRange> {
    @Override
    public BirthYearRange convert(String source) {
        try {
            return BirthYearRange.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ShoppingException(ErrorCode.INVALID_BIRTH_YEAR_RANGE);
        }
    }
}
