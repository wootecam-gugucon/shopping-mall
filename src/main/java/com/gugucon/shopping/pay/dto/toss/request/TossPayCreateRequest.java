package com.gugucon.shopping.pay.dto.toss.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public final class TossPayCreateRequest {

    private Long orderId;
}
