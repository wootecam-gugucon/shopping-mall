package com.gugucon.shopping.item.repository;

import com.gugucon.shopping.item.domain.entity.RateStat;
import com.gugucon.shopping.item.repository.dto.SimpleRateStatDto;
import com.gugucon.shopping.member.domain.vo.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RateStatRepository extends JpaRepository<RateStat, Long> {

    @Query("SELECT new com.gugucon.shopping.item.repository.dto.SimpleRateStatDto(p.id, sum(r.score), count(r.score)) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN Member m ON o.memberId = m.id " +
            "JOIN Rate r ON r.orderItem.id = oi.id " +
            "JOIN Product p ON p.id = oi.productId " +
            "WHERE m.birthDate between :startDate AND :endDate " +
            "AND m.gender = :gender " +
            "GROUP BY p.id")
    List<SimpleRateStatDto> findAllSimpleRateStatByGenderAndBirthDateBetween(@Param("gender") final Gender gender,
                                                                             @Param("startDate") final LocalDate startDate,
                                                                             @Param("endDate") final LocalDate endDate);
}
