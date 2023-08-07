package shopping.auth.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

@DisplayName("Password 단위 테스트")
class PasswordTest {

    @ParameterizedTest
    @ValueSource(strings = {"asdf", "Q1w2", "0032pi9!!0"})
    @DisplayName("올바른 비밀번호 형식(영어 소문자를 포함한 4~20자 문자열)으로 생성할 수 있다.")
    void createSuccess(final String value) {
        /* given */

        /* when & then */
        assertThatNoException().isThrownBy(() -> new Password(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"asd", "123!!", "qwerty_qwerty_qwerty_"})
    @DisplayName("올바른 비밀번호 형식이 아닌 경우 생성할 수 없다.")
    void createFailure(final String value) {
        /* given */

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
            () -> new Password(value));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD_PATTERN);
    }

    @Test
    @DisplayName("해당 비밀번호 값을 가진다.")
    void hasValue() {
        /* given */
        final Password password = new Password("test_password");

        /* when & then*/
        assertThat(password.hasValue("test_password")).isTrue();
        assertThat(password.hasValue("invalid_password")).isFalse();
    }
}
