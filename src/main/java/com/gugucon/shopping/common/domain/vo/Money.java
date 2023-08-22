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
public class Money {

    public static final Money ZERO = new Money(0L);

    @NotNull
    private Long value;

    private Money(final Long value) {
        validate(value);
        this.value = value;
    }

    public static Money from(final Long value) {
        return new Money(value);
    }

    public Money add(final Money other) {
        return new Money(value + other.value);
    }

    public Money subtract(final Money other) {
        return new Money(value - other.value);
    }

    public Money multiply(final Quantity quantity) {
        return new Money(value * quantity.getValue());
    }

    private void validate(final Long value) {
        if (value < 0) {
            throw new ShoppingException(ErrorCode.INVALID_MONEY);
        }
    }

    public boolean isNotPositive() {
        return value <= 0;
    }

    public boolean isLessThan(final Money other) {
        return value < other.value;
    }
}
