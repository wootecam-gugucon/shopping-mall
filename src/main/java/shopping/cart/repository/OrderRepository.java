package shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shopping.cart.domain.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
