package com.gugucon.shopping.item.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRecommendResponse {

    private final List<ProductDetailResponse> products;
}
