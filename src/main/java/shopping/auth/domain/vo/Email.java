package shopping.auth.domain.vo;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

@Embeddable
public class Email {

    private static final String EMAIL_REGEX = "^[\\w]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Column(unique = true, name = "email")
    private String value;

    protected Email() {
    }

    public Email(final String value) {
        validatePattern(value);

        this.value = value;
    }

    private static void validatePattern(final String value) {
        final Matcher matcher = EMAIL_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new ShoppingException(ErrorCode.INVALID_EMAIL_PATTERN);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
