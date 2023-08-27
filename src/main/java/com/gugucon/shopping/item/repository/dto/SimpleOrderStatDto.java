package com.gugucon.shopping.item.repository.dto;

import lombok.Getter;

@Getter
public class SimpleOrderStatDto {

    private Long productId;
    private Long count;

    public SimpleOrderStatDto(Long productId, Long count) {
        this.productId = productId;
        this.count = count;
    }
}
