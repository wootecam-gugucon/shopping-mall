package shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shopping.cart.domain.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
