package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.order.domain.entity.Order;

public interface OrderIdTranslator {

    String encode(final Order order);

    Long decode(final String encodedOrderId);
}
