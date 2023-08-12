package com.gugucon.shopping.common.domain.vo;

import java.util.Objects;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Quantity {

    private static final int ZERO = 0;
    private static final int MAX_QUANTITY = 1000;

    @Column(name = "quantity")
    private int value;

    protected Quantity() {
    }

    public Quantity(final int value) {
        validateRange(value);
        this.value = value;
    }

    public boolean isZero() {
        return this.value == ZERO;
    }

    private void validateRange(final int value) {
        if (isOutOfBound(value)) {
            throw new ShoppingException(ErrorCode.INVALID_QUANTITY);
        }
    }

    private boolean isOutOfBound(final int value) {
        return value < ZERO || MAX_QUANTITY < value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Quantity quantity = (Quantity) o;
        return value == quantity.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
