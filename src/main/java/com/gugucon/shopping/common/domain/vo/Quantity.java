package com.gugucon.shopping.common.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class Quantity {

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
        return value.equals(0);
    }

    private void validateRange(final Integer value) {
        if (value < 0) {
            throw new ShoppingException(ErrorCode.INVALID_QUANTITY);
        }
    }

    public boolean isLessThan(final Quantity other) {
        return this.value < other.value;
    }

    public Quantity decreaseBy(final Quantity other) {
        return Quantity.from(this.value - other.value);
    }
}
