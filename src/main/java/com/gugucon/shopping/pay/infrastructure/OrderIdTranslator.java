package com.gugucon.shopping.pay.infrastructure;

public interface OrderIdTranslator {

    public String encode(Long orderId, String orderName);
    public Long decode(String encodedOrderId);
}
