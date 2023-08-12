package com.gugucon.shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gugucon.shopping.cart.domain.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
