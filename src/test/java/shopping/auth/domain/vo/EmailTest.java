package shopping.auth.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {"valid_email99@woowafriends.com", "Qwerty@gmail.com"})
    @DisplayName("올바른 이메일 형식으로 생성할 수 있다.")
    void createSuccess(final String value) {
        /* given */

        /* when & then */
        assertThatNoException().isThrownBy(() -> new Email(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Qwerty!!@gmail.com", "Qwerty@@gmail.com", "Qwerty@gmail",
        "Qwerty@gmail.c"})
    @DisplayName("올바른 이메일 형식이 아닌 경우 생성할 수 없다.")
    void createFailure(final String value) {
        /* given */

        /* when & then */
        final ShoppingException exception = assertThrows(ShoppingException.class,
            () -> new Email(value));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMAIL_PATTERN);
    }
}
