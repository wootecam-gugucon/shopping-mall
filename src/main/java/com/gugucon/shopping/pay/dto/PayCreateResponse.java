package com.gugucon.shopping.pay.dto;

import com.gugucon.shopping.pay.domain.Pay;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PayCreateResponse {

    private final Long payId;

    public static PayCreateResponse from(final Pay pay) {
        return new PayCreateResponse(pay.getId());
    }
}
