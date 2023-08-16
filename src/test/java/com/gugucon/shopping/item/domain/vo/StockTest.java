package com.gugucon.shopping.item.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.member.domain.vo.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StockTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
    @DisplayName("재고를 생성한다.")
    void create(final int value) {
        // given

        // when & then
        assertThatNoException().isThrownBy(() -> Stock.from(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, Integer.MAX_VALUE + 1})
    @DisplayName("부적절한 값으로 재고를 생성할 때 예외가 발생한다.")
    void createFail_tooMany(final int value) {
        // given

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> Stock.from(value));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_STOCK);
    }
}
