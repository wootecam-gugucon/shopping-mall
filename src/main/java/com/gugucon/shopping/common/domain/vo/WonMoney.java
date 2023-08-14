package com.gugucon.shopping.common.domain.vo;

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
public class WonMoney {

    public static final WonMoney ZERO = new WonMoney(0L);

    @NotNull
    private Long value;

    private WonMoney(final Long value) {
        this.value = value;
    }

    public static WonMoney from(final Long value) {
        return new WonMoney(value);
    }

    public WonMoney add(final WonMoney other) {
        return new WonMoney(value + other.value);
    }

    public WonMoney multiply(final Quantity quantity) {
        return new WonMoney(value * quantity.getValue());
    }
}
