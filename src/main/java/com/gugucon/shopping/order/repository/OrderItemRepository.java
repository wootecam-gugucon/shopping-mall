package com.gugucon.shopping.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gugucon.shopping.order.domain.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
