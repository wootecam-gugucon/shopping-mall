package com.gugucon.shopping.item.dto.response;

import com.gugucon.shopping.item.domain.entity.Product;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRecommendResponse {

    private List<ProductDetailResponse> products;

    public static ProductRecommendResponse of(final List<Product> products) {
        final List<ProductDetailResponse> productDetails = products.stream()
            .map(ProductDetailResponse::from)
            .toList();
        return new ProductRecommendResponse(productDetails);
    }
}
