package com.gugucon.shopping.order.repository;

import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import com.gugucon.shopping.order.domain.entity.OrderItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM Order o INNER JOIN o.orderItems oi "
        + "WHERE o.memberId = :memberId AND o.status = :orderStatus AND oi.id = :orderItemId")
    Optional<OrderItem> findByOrderIdAndMemberIdAndOrderStatus(@Param("memberId") Long memberId,
                                                               @Param("orderItemId") Long orderItemId,
                                                               @Param("orderStatus") OrderStatus orderStatus);
}
