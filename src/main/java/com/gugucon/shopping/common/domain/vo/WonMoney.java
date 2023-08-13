package com.gugucon.shopping.common.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class WonMoney {

    public static final WonMoney ZERO = new WonMoney(0);

    private long value;

    private WonMoney(final long value) {
        this.value = value;
    }

    public static WonMoney from(final long value) {
        return new WonMoney(value);
    }

    public WonMoney add(final WonMoney other) {
        return new WonMoney(value + other.value);
    }

    public WonMoney multiply(final Quantity quantity) {
        return new WonMoney(value * quantity.getValue());
    }
}
