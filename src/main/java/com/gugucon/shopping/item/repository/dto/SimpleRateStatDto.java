package com.gugucon.shopping.item.repository.dto;

import lombok.Getter;

@Getter
public class SimpleRateStatDto {

    private Long productId;
    private Long totalScore;
    private Long count;

    public SimpleRateStatDto(Long productId, Long totalScore, Long count) {
        this.productId = productId;
        this.totalScore = totalScore;
        this.count = count;
    }
}
