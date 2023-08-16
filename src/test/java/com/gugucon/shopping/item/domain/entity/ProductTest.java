package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.TestUtils;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @Test
    @DisplayName("물품이 품절인지 검사한다.")
    void validateStock() {
        // given
        final Product product = TestUtils.createProduct("name", 1000L);

        // when & then
        assertThatNoException().isThrownBy(product::validateStock);
    }

    @Test
    @DisplayName("물품이 품절인지 검사할 때 품절이면 예외를 반환한다.")
    void throwException_validateStock() {
        // given
        final Product product = TestUtils.createSoldOutProduct("name", 1000L);

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, product::validateStock);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SOLD_OUT);
    }
}
