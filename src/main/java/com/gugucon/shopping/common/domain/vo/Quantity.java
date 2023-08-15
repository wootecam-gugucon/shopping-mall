package com.gugucon.shopping.common.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class Quantity {

    private static final Integer MIN_VALUE = 0;
    private static final Integer MAX_VALUE = 1000;

    @NotNull
    private Integer value;

    private Quantity(final Integer value) {
        validateRange(value);
        this.value = value;
    }

    public static Quantity from(final Integer value) {
        return new Quantity(value);
    }

    public boolean isZero() {
        return Objects.equals(this.value, MIN_VALUE);
    }

    private void validateRange(final Integer value) {
        if (isOutOfBound(value)) {
            throw new ShoppingException(ErrorCode.INVALID_QUANTITY);
        }
    }

    private boolean isOutOfBound(final Integer value) {
        return value < MIN_VALUE || MAX_VALUE < value;
    }

    public Integer getValue() {
        return value;
    }
}
