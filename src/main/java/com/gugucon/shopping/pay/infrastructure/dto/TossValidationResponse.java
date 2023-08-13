package com.gugucon.shopping.pay.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class TossValidationResponse {

    private final String paymentKey;
    private final String status;
    private final String orderId;
    private final String orderName;
    private final String method;

    @JsonCreator
    public TossValidationResponse(@JsonProperty("paymentKey") final String paymentKey,
                                  @JsonProperty("status") final String status,
                                  @JsonProperty("orderId") final String orderId,
                                  @JsonProperty("orderName") final String orderName,
                                  @JsonProperty("method") final String method) {
        this.paymentKey = paymentKey;
        this.status = status;
        this.orderId = orderId;
        this.orderName = orderName;
        this.method = method;
    }
}
