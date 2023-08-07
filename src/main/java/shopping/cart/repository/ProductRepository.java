package shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shopping.cart.domain.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
