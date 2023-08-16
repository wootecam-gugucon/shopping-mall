package com.gugucon.shopping.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PayPageRequest {

    private Long orderId;
    private String orderName;
    private Long price;
}
