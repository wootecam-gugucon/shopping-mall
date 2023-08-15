package com.gugucon.shopping.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ShoppingException extends RuntimeException {

    private final ErrorCode errorCode;
}
