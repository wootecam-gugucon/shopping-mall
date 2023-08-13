package com.gugucon.shopping.member.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Password 단위 테스트")
class PasswordTest {

    @ParameterizedTest
    @ValueSource(strings = {"asdf", "Q1w2", "0032pi9!!0"})
    @DisplayName("비밀번호를 생성한다.")
    void create(final String value) {
        /* given */

        /* when & then */
        assertThatNoException().isThrownBy(() -> Password.from(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"asd", "123!!", "qwerty_qwerty_qwerty_"})
    @DisplayName("올바른 비밀번호 형식이 아닌 경우 비밀번호를 생성할 때 예외가 발생한다.")
    void createFail_invalidPattern(final String value) {
        /* given */

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
                () -> Password.from(value));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD_PATTERN);
    }

    @Test
    @DisplayName("해당 비밀번호 값을 가진다.")
    void hasValue() {
        /* given */
        final Password password = Password.from("test_password");

        /* when & then*/
        assertThat(password.hasValue("test_password")).isTrue();
        assertThat(password.hasValue("invalid_password")).isFalse();
    }
}
