package com.gugucon.shopping.point.service;

import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.point.domain.Point;
import com.gugucon.shopping.point.dto.request.PointChargeRequest;
import com.gugucon.shopping.point.dto.response.PointResponse;
import com.gugucon.shopping.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public void charge(final PointChargeRequest pointChargeRequest, final Long memberId) {
        final Point point = pointRepository.findByMemberId(memberId)
                                           .orElseGet(() -> pointRepository.save(Point.from(memberId)));
        point.charge(Money.from(pointChargeRequest.getPoint()));
    }

    public PointResponse getCurrentPoint(final Long memberId) {
        final Point point = pointRepository.findByMemberId(memberId)
                                           .orElseGet(() -> pointRepository.save(Point.from(memberId)));
        return PointResponse.from(point);
    }
}
