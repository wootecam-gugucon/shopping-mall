package com.gugucon.shopping.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gugucon.shopping.item.domain.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
