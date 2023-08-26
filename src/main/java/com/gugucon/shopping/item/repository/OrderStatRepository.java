package com.gugucon.shopping.item.repository;

import com.gugucon.shopping.item.domain.entity.OrderStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatRepository extends JpaRepository<OrderStat, Long> {
}
