package com.gugucon.shopping.item.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

class ProductTest {

    @Test
    @DisplayName("물품이 품절인지 검사한다.")
    void validateStock() {
        // given
        final Product product = Product.builder()
                .name("")
                .price(1000L)
                .description("description")
                .imageFileName("default.png")
                .stock(1)
                .build();

        // when & then
        assertThatNoException().isThrownBy(product::validateStock);
    }
}
