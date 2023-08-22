package com.gugucon.shopping.rate.dto.response;

import com.gugucon.shopping.rate.domain.entity.Rate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RateDetailResponse {

    private short score;

    public static RateDetailResponse from(final Rate rate) {
        return new RateDetailResponse(rate.getScore());
    }
}
