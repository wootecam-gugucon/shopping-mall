package com.gugucon.shopping.pay.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchException;

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
                .price(1000L)
                .build());
    }

    @Test
    @DisplayName("금액이 같은지 확인한다")
    void validateMoneySuccess_PriceSame() {
        // given
        Pay pay = Pay.builder()
                .orderId(1L)
                .price(1000L)
                .build();

        // when & then
        assertThatNoException().isThrownBy(() -> pay.validateMoney(Money.from(1000L)));
    }

    @Test
    @DisplayName("금액이 같은지 확인하고 다르면 예외를 던진다")
    void validateMoneyFail_PriceDifferent() {
        // given
        Pay pay = Pay.builder()
                .orderId(1L)
                .price(1000L)
                .build();

        // when
        Exception exception = catchException(() -> pay.validateMoney(Money.from(500L)));

        // then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
