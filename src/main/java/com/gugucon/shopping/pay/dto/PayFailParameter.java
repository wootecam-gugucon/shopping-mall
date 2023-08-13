package com.gugucon.shopping.pay.dto;

public final class PayFailParameter {

    private final String errorCode;
    private final String message;
    private final String orderId;

    public PayFailParameter(String errorCode, String message, String orderId) {
        this.errorCode = errorCode;
        this.message = message;
        this.orderId = orderId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getOrderId() {
        return orderId;
    }
}
