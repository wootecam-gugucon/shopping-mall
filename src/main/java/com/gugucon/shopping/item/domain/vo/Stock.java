package com.gugucon.shopping.item.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Stock {

    private int value;

    public static Stock from(final int value) {
        validate(value);
        return new Stock(value);
    }

    private static void validate(final int value) {
        if (value < 0) {
            throw new ShoppingException(ErrorCode.INVALID_STOCK);
        }
    }
}
