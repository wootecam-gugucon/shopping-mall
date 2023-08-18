package com.gugucon.shopping.item.repository;

import com.gugucon.shopping.item.domain.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT c FROM CartItem c "
            + "JOIN FETCH c.product "
            + "WHERE c.memberId = :memberId")
    List<CartItem> findAllByMemberIdWithProduct(@Param("memberId") Long memberId);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    void deleteAllByMemberId(Long memberId);
}
