package com.gugucon.shopping.item.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Stock {

    private int value;

    public static Stock from(final int value) {
        throw new UnsupportedOperationException();
    }
}
