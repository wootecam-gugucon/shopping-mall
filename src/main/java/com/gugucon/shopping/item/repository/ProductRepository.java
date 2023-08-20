package com.gugucon.shopping.item.repository;

import com.gugucon.shopping.item.domain.entity.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByNameContainingIgnoreCase(final @NotNull String name, final Pageable pageable);
}
