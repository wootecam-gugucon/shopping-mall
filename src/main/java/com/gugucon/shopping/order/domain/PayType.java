package com.gugucon.shopping.order.domain;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;

public enum PayType {
    POINT,
    TOSS,
    NONE;

    public static PayType from(final String type) {
        try {
            return PayType.valueOf(type);
        } catch (IllegalArgumentException exception) {
            throw new ShoppingException(ErrorCode.INVALID_PAY_TYPE);
        }
    }
}
