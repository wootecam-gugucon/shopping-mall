package com.gugucon.shopping.pay.dto;

import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.pay.domain.Pay;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PayInfoResponse {

    private final String encodedOrderId;
    private final String orderName;
    private final Long price;
    private final String customerEmail;
    private final String customerName;
    private final String customerKey;
    private final String successUrl;
    private final String failUrl;

    public static PayInfoResponse from(final String encodedOrderId,
                                       final Order order,
                                       final Pay pay,
                                       final Member member,
                                       final String customerKey,
                                       final String successUrl,
                                       final String failUrl) {
        return new PayInfoResponse(encodedOrderId,
                                   order.getOrderName(),
                                   pay.getPrice().getValue(),
                                   member.getEmail().getValue(),
                                   member.getNickname().getValue(),
                                   customerKey,
                                   successUrl,
                                   failUrl);
    }
}
