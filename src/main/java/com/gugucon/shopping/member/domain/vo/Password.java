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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class Password {

    private static final String PASSWORD_REGEX = "^(?=.*[a-z]).{4,20}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    @Column(name = "password", nullable = false)
    @NotNull
    private String value;

    private Password(final String value) {
        validatePattern(value);

        this.value = value;
    }

    public static Password from(final String value) {
        return new Password(value);
    }

    private static void validatePattern(final String value) {
        final Matcher matcher = PASSWORD_PATTERN.matcher(value);

        if (!matcher.matches()) {
            throw new ShoppingException(ErrorCode.INVALID_PASSWORD_PATTERN);
        }
    }

    public boolean hasValue(final String value) {
        return Objects.equals(this.value, value);
    }
}
