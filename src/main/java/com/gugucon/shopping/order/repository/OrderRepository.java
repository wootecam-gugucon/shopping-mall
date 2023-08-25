package com.gugucon.shopping.order.repository;

import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.orderItems " +
            "WHERE o.memberId = :memberId and o.status = :status")
    List<Order> findAllByMemberIdAndStatusWithOrderItems(@Param("memberId") Long memberId,
                                                         @Param("status") OrderStatus status,
                                                         Sort sort);

    Optional<Order> findByIdAndMemberId(Long id, Long memberId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.orderItems " +
            "WHERE o.status IN :statuses AND (o.lastModifiedAt BETWEEN :start AND :end)")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Order> findAllByStatusInAndLastModifiedAtBetweenWithOrderItems(
            @Param("statuses") List<OrderStatus> statuses,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
