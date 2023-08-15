package com.gugucon.shopping.member.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class Email {

    private static final String EMAIL_REGEX = "^[\\w]+@[a-zA-Z0-9]+\\.[a-zA-Z.]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Column(unique = true, name = "email", nullable = false)
    @NotNull
    private String value;

    private Email(final String value) {
        validatePattern(value);

        this.value = value;
    }

    public static Email from(final String value) {
        return new Email(value);
    }

    private static void validatePattern(final String value) {
        final Matcher matcher = EMAIL_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new ShoppingException(ErrorCode.INVALID_EMAIL_PATTERN);
        }
    }
}
