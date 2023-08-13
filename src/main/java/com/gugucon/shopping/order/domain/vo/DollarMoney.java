package com.gugucon.shopping.order.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Getter
public class DollarMoney {

    private final double value;

    public static DollarMoney from(final double value) {
        return new DollarMoney(value);
    }
}
