package com.gugucon.shopping.item.repository;

import com.gugucon.shopping.item.domain.entity.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByNameContainingIgnoreCase(final @NotNull String name, final Pageable pageable);

    @Query("select p from Product p " +
            "left join OrderItem oi on p.id = oi.productId " +
            "where p.name like %:keyword% " +
            "group by p.id " +
            "order by sum(oi.quantity.value) desc ")
    Page<Product> findAllByNameSortByOrderCountDesc(@Param("keyword") final String keyword, final Pageable pageable);

    @Query("select p from Product p " +
            "left join OrderItem oi on oi.productId = p.id " +
            "left join Rate r on r.orderItem.id = oi.id " +
            "where p.name like %:keyword% " +
            "group by p.id " +
            "order by avg(r.score) desc ")
    Page<Product> findAllByNameSortByRateDesc(@Param("keyword") final String keyword, final Pageable pageable);
}
