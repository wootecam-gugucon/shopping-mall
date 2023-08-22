package com.gugucon.shopping.point.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Point 단위 테스트")
class PointTest {

    @Test
    @DisplayName("포인트 객체를 생성한다.")
    void create() {
        assertThatNoException()
                .isThrownBy(() -> Point.builder()
                                       .memberId(1L)
                                       .point(Money.from(1000L))
                                       .build());
    }

    @Test
    @DisplayName("포인트를 충전한다.")
    void chargePoint() {
        // given
        final Point point = Point.builder()
                                 .memberId(1L)
                                 .point(Money.from(1000L))
                                 .build();

        // when
        point.charge(Money.from(1000L));

        // then
        assertThat(point.getPoint()).isEqualTo(Money.from(2000L));
    }

    @Test
    @DisplayName("0 이하의 포인트를 충전하면 예외를 던진다.")
    void chargePointFail_notPositivePoint() {
        // given
        final Point point = Point.builder()
                                 .memberId(1L)
                                 .point(Money.from(1000L))
                                 .build();
        final Money charge = Money.from(0L);

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> point.charge(charge));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_CHARGE_NOT_POSITIVE);
    }

    @Test
    @DisplayName("포인트를 사용한다.")
    void use() {
        // given
        final Point point = Point.builder()
                                 .memberId(1L)
                                 .point(Money.from(1000L))
                                 .build();
        final Money use = Money.from(300L);

        // when
        point.use(use);

        // then
        assertThat(point.getPoint()).isEqualTo(Money.from(700L));
    }

    @Test
    @DisplayName("현재 잔액보다 많은 포인트를 사용하면 예외를 던진다.")
    void useFail_overCurrentBalance() {
        // given
        final Point point = Point.builder()
                                 .memberId(1L)
                                 .point(Money.from(1000L))
                                 .build();
        final Money use = Money.from(1200L);

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class, () -> point.use(use));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_NOT_ENOUGH);
    }
}