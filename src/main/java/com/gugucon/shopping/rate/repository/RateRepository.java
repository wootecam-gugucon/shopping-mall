package com.gugucon.shopping.rate.repository;

import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.member.domain.vo.Gender;
import com.gugucon.shopping.rate.domain.entity.Rate;
import com.gugucon.shopping.rate.repository.dto.AverageRateDto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByOrderItemId(final Long orderItemId);

    @Query("SELECT new com.gugucon.shopping.rate.repository.dto.AverageRateDto(sum(rs.count), sum(rs.totalScore)) "
        + "FROM RateStat rs "
        + "WHERE rs.productId = :productId")
    AverageRateDto findScoresByProductId(final Long productId);

    @Query("SELECT r.score FROM Order o " +
            "INNER JOIN o.orderItems oi " +
            "INNER JOIN Rate r ON oi.id = r.orderItem.id " +
            "WHERE o.memberId = :memberId " +
            "AND oi.id = :orderItemId")
    Optional<Integer> findByMemberIdAndOrderItemId(final Long memberId, final Long orderItemId);

    @Query("SELECT new com.gugucon.shopping.rate.repository.dto.AverageRateDto(rs.count, rs.totalScore) " +
            "FROM RateStat rs " +
            "WHERE rs.productId = :productId " +
            "AND rs.gender = :gender " +
            "AND rs.birthYearRange = :birthYearRange")
    AverageRateDto findScoresByMemberGenderAndMemberBirthYear(final Long productId,
                                                              final Gender gender,
                                                              final BirthYearRange birthYearRange);
}
