package com.gugucon.shopping.item.repository;

import com.gugucon.shopping.item.domain.entity.Rate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByOrderItemId(final Long orderItemId);
}
