package com.gugucon.shopping.pay.dto.point.response;

import com.gugucon.shopping.pay.domain.Pay;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PointPayResponse {

    private Long orderId;

    public static PointPayResponse from(final Pay pay) {
        return new PointPayResponse(pay.getOrderId());
    }
}
