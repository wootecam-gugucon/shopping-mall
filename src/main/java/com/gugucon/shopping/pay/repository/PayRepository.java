package com.gugucon.shopping.pay.repository;

import com.gugucon.shopping.pay.domain.Pay;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayRepository extends JpaRepository<Pay, Long> {

    Optional<Pay> findByEncodedOrderId(String encodedOrderId);
}
