package com.gugucon.shopping.item.dto.response;

import com.gugucon.shopping.item.domain.entity.Product;
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

    public static ProductDetailResponse from(Product product) {
        return new ProductDetailResponse(
            product.getId(),
            product.getName(),
            product.getImageFileName(),
            product.getDescription(),
            product.getPrice().getValue()
        );
    }
}
