package com.gugucon.shopping.order.repository;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.gugucon.shopping.order.domain.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o " +
        "LEFT OUTER JOIN FETCH o.orderItems " +
        "WHERE o.userId = :userId")
    List<Order> findAllByUserIdWithOrderItems(@Param("userId") Long userId, Sort sort);
}
