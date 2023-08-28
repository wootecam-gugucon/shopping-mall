package com.gugucon.shopping.rate.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RateDetailResponse {

    private int score;

    public static RateDetailResponse from(final int rate) {
        return new RateDetailResponse(rate);
    }
}
