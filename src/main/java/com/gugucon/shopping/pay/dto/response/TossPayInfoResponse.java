package com.gugucon.shopping.pay.dto.response;

import com.gugucon.shopping.order.domain.entity.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TossPayInfoResponse {

    private final String encodedOrderId;
    private final String orderName;
    private final Long price;
    private final String customerKey;
    private final String successUrl;
    private final String failUrl;

    public static TossPayInfoResponse from(final String encodedOrderId,
                                           final Order order,
                                           final String customerKey,
                                           final String successUrl,
                                           final String failUrl) {
        return new TossPayInfoResponse(encodedOrderId,
                                       order.createOrderName(),
                                       order.calculateTotalPrice().getValue(),
                                       customerKey,
                                       successUrl,
                                       failUrl);
    }
}
