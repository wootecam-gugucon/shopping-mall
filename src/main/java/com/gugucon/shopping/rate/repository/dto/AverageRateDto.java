package com.gugucon.shopping.rate.repository.dto;

import lombok.Getter;

@Getter
public class AverageRateDto {

    private final Long count;
    private final Long totalScore;

    public AverageRateDto(final Long count, final Long totalScore) {
        this.count = count;
        this.totalScore = totalScore;
    }
}
