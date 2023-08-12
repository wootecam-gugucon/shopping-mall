package com.gugucon.shopping.pay.dto;

public final class PayResponse {

    private final String encodedOrderId;
    private final String orderName;
    private final String successUrl = "http://localhost:8080/pay/success";
    private final String failUrl = "http://localhost:8080/pay/fail";
    private final String customerEmail = "asdf@asdf.asdf";
    private final String customerName = "김동주";

    public PayResponse(final String encodedOrderId, final String orderName) {
        this.encodedOrderId = encodedOrderId;
        this.orderName = orderName;
    }

    public String getEncodedOrderId() {
        return encodedOrderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getFailUrl() {
        return failUrl;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }
}
