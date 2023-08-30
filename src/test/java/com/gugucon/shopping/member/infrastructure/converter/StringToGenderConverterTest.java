package com.gugucon.shopping.member.infrastructure.converter;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringToGenderConverterTest {

    private final StringToGenderConverter converter = new StringToGenderConverter();

    @Test
    @DisplayName("잘못된 문자열을 입력하면 변환 시 예외가 발생한다.")
    void convertFail_invalidInput() {
        // given
        final String invalidInput = "invalid_input";

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> converter.convert(invalidInput));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_GENDER);
    }
}
