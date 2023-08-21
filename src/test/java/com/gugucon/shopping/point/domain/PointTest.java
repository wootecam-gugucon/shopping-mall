package com.gugucon.shopping.point.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchException;

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
                                       .point(1000L)
                                       .build());
    }

    @Test
    @DisplayName("포인트를 충전한다.")
    void chargePoint() {
        // given
        final Point point = Point.builder()
                                 .memberId(1L)
                                 .point(1000L)
                                 .build();

        // when
        final Point charged = point.charge(1000L);

        // then
        assertThat(charged.getPoint()).isEqualTo(2000L);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    @DisplayName("0 이하의 포인트를 충전하면 예외를 던진다.")
    void chargePointFail_notPositivePoint(long chargePoint) {
        // given
        final Point point = Point.builder()
                                 .memberId(1L)
                                 .point(1000L)
                                 .build();

        // when
        final Exception exception = catchException(() -> point.charge(chargePoint));

        // then
        assertThat(exception).isInstanceOf(ShoppingException.class);
        assertThat(((ShoppingException) exception).getErrorCode()).isEqualTo(ErrorCode.POINT_CHARGE_NOT_POSITIVE);
    }

    @Test
    @DisplayName("포인트를 사용한다.")
    void use() {
        // given
        final Point point = Point.builder()
                                 .memberId(1L)
                                 .point(1000L)
                                 .build();

        // when
        final Point used = point.use(300L);

        // then
        assertThat(used.getPoint()).isEqualTo(700L);
    }

    @Test
    @DisplayName("현재 잔액보다 많은 포인트를 사용하면 예외를 던진다.")
    void useFail_overCurrentBalance() {
        // given
        final Point point = Point.builder()
                                 .memberId(1L)
                                 .point(1000L)
                                 .build();

        // when
        final Exception exception = catchException(() -> point.use(1200L));

        // then
        assertThat(exception).isInstanceOf(ShoppingException.class);
        assertThat(((ShoppingException) exception).getErrorCode()).isEqualTo(ErrorCode.POINT_NOT_ENOUGH);
    }
}