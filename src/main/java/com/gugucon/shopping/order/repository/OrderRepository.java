package com.gugucon.shopping.order.repository;

import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT OUTER JOIN FETCH o.orderItems " +
            "WHERE o.memberId = :memberId and o.status = :status")
    List<Order> findAllByMemberIdAndStatusWithOrderItems(@Param("memberId") Long memberId,
                                                         @Param("status") OrderStatus status,
                                                         Sort sort);
}
