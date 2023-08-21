package com.gugucon.shopping.item.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductDetailResponse {

    private Long id;
    private String name;
    private String imageFileName;
    private String description;
    private long price;
}
