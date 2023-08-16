package com.gugucon.shopping.pay.dto;

import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PayInfoResponse {

    private final String customerEmail;
    private final String customerName;
    private final String orderName;
    private final String encodedOrderId;
    private final Long price;
    private final String successUrl;
    private final String failUrl;

    public static PayInfoResponse from(final Member member,
                                       final String orderName,
                                       final String encodedOrderId,
                                       final WonMoney price,
                                       final String successUrl,
                                       final String failUrl) {
        return new PayInfoResponse(member.getEmail().getValue(),
                                   member.getNickname().getValue(),
                                   orderName,
                                   encodedOrderId,
                                   price.getValue(),
                                   successUrl,
                                   failUrl);
    }
}
