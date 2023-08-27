package com.gugucon.shopping.item.repository;

import com.gugucon.shopping.item.domain.entity.OrderStat;
import com.gugucon.shopping.item.repository.dto.SimpleOrderStatDto;
import com.gugucon.shopping.member.domain.vo.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderStatRepository extends JpaRepository<OrderStat, Long> {

    @Query("SELECT new com.gugucon.shopping.item.repository.dto.SimpleOrderStatDto(p.id, sum(oi.quantity.value)) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN Member m ON o.memberId = m.id " +
            "JOIN Product p ON p.id = oi.productId " +
            "WHERE m.birthDate between :startDate AND :endDate " +
            "AND m.gender = :gender " +
            "GROUP BY p.id")
    List<SimpleOrderStatDto> findAllSimpleOrderStatByGenderAndBirthDateBetween(@Param("gender") final Gender gender,
                                                                               @Param("startDate") final LocalDate startDate,
                                                                               @Param("endDate") final LocalDate endDate);
}
