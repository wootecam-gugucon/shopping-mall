package com.gugucon.shopping.rate.repository;

import com.gugucon.shopping.member.domain.vo.Gender;
import com.gugucon.shopping.rate.domain.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByOrderItemId(final Long orderItemId);

    @Query("SELECT r.score FROM Rate r INNER JOIN r.orderItem oi WHERE oi.productId = :productId")
    List<Integer> findScoresByProductId(final Long productId);

    @Query("SELECT r.score FROM Order o " +
            "INNER JOIN o.orderItems oi " +
            "INNER JOIN Rate r ON oi.id = r.orderItem.id " +
            "WHERE o.memberId = :memberId " +
            "AND oi.id = :orderItemId")
    Optional<Integer> findByMemberIdAndOrderItemId(final Long memberId, final Long orderItemId);

    @Query("SELECT r.score FROM Member m " +
            "INNER JOIN Order o ON o.memberId = m.id " +
            "INNER JOIN o.orderItems oi " +
            "INNER JOIN Rate r ON oi.id = r.orderItem.id " +
            "WHERE oi.productId = :productId " +
            "AND m.gender = :gender " +
            "AND m.birthDate >= :startDate AND m.birthDate <= :endDate")
    List<Integer> findScoresByMemberGenderAndMemberBirthYear(final Long productId,
                                                             final Gender gender,
                                                             final LocalDate startDate,
                                                             final LocalDate endDate);
}
