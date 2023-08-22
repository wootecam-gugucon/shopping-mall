package com.gugucon.shopping.point.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        final Optional<Point> before = pointRepository.findByMemberId(memberId);
        pointService.charge(pointChargeRequest, memberId);
        final Optional<Point> after = pointRepository.findByMemberId(memberId);

        // then
        assertThat(before).isEmpty();
        assertThat(after).isPresent()
                         .get()
                         .extracting(Point::getMemberId, Point::getPoint)
                         .containsExactly(memberId, Money.from(1000L));
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
                                  .point(Money.from(1000L))
                                  .build());

        // when
        pointService.charge(pointChargeRequest, memberId);
        final Optional<Point> after = pointRepository.findByMemberId(memberId);

        // then
        assertThat(after).isPresent()
                         .get()
                         .extracting(Point::getMemberId, Point::getPoint)
                         .containsExactly(memberId, Money.from(2000L));
    }
}
