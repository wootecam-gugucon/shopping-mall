package com.gugucon.shopping.item.dto.response;

import com.gugucon.shopping.item.domain.entity.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ProductResponse {

    private Long id;
    private String name;
    private String imageFileName;
    private long price;

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getImageFileName(),
                product.getPrice().getValue()
        );
    }
}
