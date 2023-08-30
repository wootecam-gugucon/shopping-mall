package com.gugucon.shopping.point.repository;

import com.gugucon.shopping.point.domain.Point;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByMemberId(final Long memberId);
}
