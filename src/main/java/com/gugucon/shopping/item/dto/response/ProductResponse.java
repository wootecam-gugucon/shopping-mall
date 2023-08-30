package com.gugucon.shopping.item.dto.response;

import com.gugucon.shopping.item.domain.entity.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductResponse {

    private Long id;
    private String name;
    private String imageFileName;
    private int stock;
    private long price;

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getImageFileName(),
                product.getStock().getValue(),
                product.getPrice().getValue()
        );
    }
}
