package com.gugucon.shopping.rate.repository;

import com.gugucon.shopping.rate.domain.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByOrderItemId(final Long orderItemId);

    @Query("SELECT r FROM Rate r INNER JOIN r.orderItem oi WHERE oi.productId = :productId")
    List<Rate> findByProductId(final Long productId);

    @Query("SELECT r FROM Order o " +
            "INNER JOIN o.orderItems oi " +
            "INNER JOIN Rate r ON oi.id = r.orderItem.id " +
            "WHERE o.memberId = :memberId " +
            "AND oi.id = :orderItemId")
    Optional<Rate> findByMemberIdAndOrderItemId(final Long memberId, final Long orderItemId);
}
