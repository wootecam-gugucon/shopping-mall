package com.gugucon.shopping.point.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import com.gugucon.shopping.common.config.JpaConfig;
import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.point.domain.Point;
import com.gugucon.shopping.point.dto.request.PointChargeRequest;
import com.gugucon.shopping.point.repository.PointRepository;
import com.gugucon.shopping.utils.DomainUtils;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import({PointService.class, JpaConfig.class})
@DataJpaTest
@DisplayName("PointService 통합 테스트")
class PointServiceTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("포인트가 존재하지 않던 회원이 포인트 충전을 요청하면 포인트를 새로 생성한다.")
    void chargeSuccess_noPointBefore() {
        // given
        final PointChargeRequest pointChargeRequest = new PointChargeRequest(1000L);
        final Member member = DomainUtils.createMember();
        final Long memberId = memberRepository.save(member).getId();

        // when
        Optional<Point> before = pointRepository.findByMemberId(memberId);
        pointService.charge(pointChargeRequest, memberId);
        Optional<Point> after = pointRepository.findByMemberId(memberId);

        // then
        assertThat(before).isEmpty();
        assertThat(after).isPresent()
                         .get()
                         .extracting(Point::getMemberId, Point::getPoint)
                         .containsExactly(memberId, 1000L);
    }

    @Test
    @DisplayName("포인트가 있던 회원이 포인트 충전을 요청하면 포인트를 더하여 저장한다.")
    void chargeSuccess_pointExistBefore() {
        // given
        final PointChargeRequest pointChargeRequest = new PointChargeRequest(1000L);
        final Member member = DomainUtils.createMember();
        final Long memberId = memberRepository.save(member).getId();
        pointRepository.save(Point.builder()
                                  .memberId(memberId)
                                  .point(1000L)
                                  .build());

        // when
        pointService.charge(pointChargeRequest, memberId);
        Optional<Point> after = pointRepository.findByMemberId(memberId);

        // then
        assertThat(after).isPresent()
                         .get()
                         .extracting(Point::getMemberId, Point::getPoint)
                         .containsExactly(memberId, 2000L);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    @DisplayName("0 이하의 포인트 충전을 요청하면 예외를 던진다.")
    void chargeFail_notPositivePoint(Long chargePoint) {
        final PointChargeRequest pointChargeRequest = new PointChargeRequest(chargePoint);
        final Member member = DomainUtils.createMember();
        final Long memberId = memberRepository.save(member).getId();

        // when
        Exception exception = catchException(() -> pointService.charge(pointChargeRequest, memberId));

        // then
        assertThat(exception).isInstanceOf(ShoppingException.class);
        assertThat(((ShoppingException) exception).getErrorCode()).isEqualTo(ErrorCode.POINT_CHARGE_NOT_POSITIVE);
    }
}