package com.gugucon.shopping.pay.dto.response;

import com.gugucon.shopping.pay.domain.Pay;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PayCreateResponse {

    private Long payId;

    public static PayCreateResponse from(final Pay pay) {
        return new PayCreateResponse(pay.getId());
    }
}
