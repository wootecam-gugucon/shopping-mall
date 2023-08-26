package com.gugucon.shopping.order.repository;

import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByMemberIdAndStatus(Long memberId, OrderStatus status, Pageable pageable);

    Optional<Order> findByIdAndMemberId(Long id, Long memberId);
}
