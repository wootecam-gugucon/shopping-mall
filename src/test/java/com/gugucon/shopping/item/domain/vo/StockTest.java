package com.gugucon.shopping.item.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;

class StockTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
    @DisplayName("재고를 생성한다.")
    void create(final int value) {
        // given

        // when & then
        assertThatNoException().isThrownBy(() -> Stock.from(value));
    }
}
