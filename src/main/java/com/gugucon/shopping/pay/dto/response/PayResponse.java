package com.gugucon.shopping.pay.dto.response;

import com.gugucon.shopping.pay.domain.Pay;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PayResponse {

    private Long orderId;

    public static PayResponse from(final Pay pay) {
        return new PayResponse(pay.getOrderId());
    }
}
