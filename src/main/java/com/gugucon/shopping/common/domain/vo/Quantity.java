package com.gugucon.shopping.common.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class Quantity {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 1000;

    private int value;

    private Quantity(final int value) {
        validateRange(value);
        this.value = value;
    }

    public static Quantity from(final int value) {
        return new Quantity(value);
    }

    public boolean isZero() {
        return this.value == MIN_VALUE;
    }

    private void validateRange(final int value) {
        if (isOutOfBound(value)) {
            throw new ShoppingException(ErrorCode.INVALID_QUANTITY);
        }
    }

    private boolean isOutOfBound(final int value) {
        return value < MIN_VALUE || MAX_VALUE < value;
    }

    public int getValue() {
        return value;
    }
}
