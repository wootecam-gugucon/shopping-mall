package com.gugucon.shopping.order.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ExchangeRateResponse {

    private boolean success;
    private String source;
    private Map<String, Double> quotes;
}
