package com.gugucon.shopping.point.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchException;

import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        final Point charged = point.charge(Money.from(1000L));

        // then
        assertThat(charged.getPoint()).isEqualTo(Money.from(2000L));
    }

    @Test
    @DisplayName("0 포인트를 충전하면 예외를 던진다.")
    void chargePointFail_notPositivePoint() {
        // given
        final Point point = Point.builder()
                                 .memberId(1L)
                                 .point(Money.from(1000L))
                                 .build();

        // when
        final Money chargeMoney = Money.from(0L);
        final Exception exception = catchException(() -> point.charge(chargeMoney));

        // then
        assertThat(exception).isInstanceOf(ShoppingException.class);
        assertThat(((ShoppingException) exception).getErrorCode()).isEqualTo(ErrorCode.POINT_CHARGE_NOT_POSITIVE);
    }
}