package com.gugucon.shopping.rate.repository;

import com.gugucon.shopping.rate.domain.entity.Rate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByOrderItemId(final Long orderItemId);

    @Query("SELECT r FROM Rate r INNER JOIN r.orderItem oi WHERE oi.productId = :productId")
    List<Rate> findByProductId(final Long productId);

    Optional<Rate> findByMemberIdAndOrderItemId(final Long memberId, final Long orderItemId);
}
