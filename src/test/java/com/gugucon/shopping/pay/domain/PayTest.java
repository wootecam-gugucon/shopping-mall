package com.gugucon.shopping.pay.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;

import com.gugucon.shopping.common.domain.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pay 단위 테스트")
class PayTest {

    @Test
    @DisplayName("결제 객체를 생성한다")
    void create() {
        assertThatNoException().isThrownBy(() -> Pay.builder()
                .orderId(1L)
                .build());
    }
}
