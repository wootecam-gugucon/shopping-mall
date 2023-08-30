package com.gugucon.shopping.order.dto.request;

import com.gugucon.shopping.order.domain.PayType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderPayRequest {

    private Long orderId;
    private PayType payType;
}
