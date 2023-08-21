package com.gugucon.shopping.point.service;

import com.gugucon.shopping.point.domain.Point;
import com.gugucon.shopping.point.dto.request.PointChargeRequest;
import com.gugucon.shopping.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public void charge(final PointChargeRequest pointChargeRequest, final Long memberId) {
        final Point point = pointRepository.findByMemberId(memberId)
                                           .orElse(Point.from(memberId));
        final Point charged = point.charge(pointChargeRequest.getPoint());
        pointRepository.save(charged);
    }
}
