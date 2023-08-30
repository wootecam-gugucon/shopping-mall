package com.gugucon.shopping.member.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Email 단위 테스트")
class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {"valid_email99@woowafriends.com", "Qwerty@gmail.com"})
    @DisplayName("이메일을 생성한다.")
    void create(final String value) {
        /* given */

        /* when & then */
        assertThatNoException().isThrownBy(() -> Email.from(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Qwerty!!@gmail.com", "Qwerty@@gmail.com", "Qwerty@gmail",
            "Qwerty@gmail.c"})
    @DisplayName("올바른 이메일 형식이 아닌 경우 이메일을 생성할 때 예외가 발생한다.")
    void createFail_invalidPattern(final String value) {
        /* given */

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
                () -> Email.from(value));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMAIL_PATTERN);
    }
}
