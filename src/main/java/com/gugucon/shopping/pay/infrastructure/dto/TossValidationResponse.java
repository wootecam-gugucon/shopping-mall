package com.gugucon.shopping.pay.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class TossValidationResponse {

    private String paymentKey;
    private String status;
    private String orderId;
    private String orderName;
    private String method;
}
