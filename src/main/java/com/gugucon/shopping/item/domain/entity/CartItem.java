package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    @NotNull
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotNull
    private Product product;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity"))
    @Valid
    @NotNull
    private Quantity quantity;

    @Builder
    private CartItem(final Long id,
                     final Long memberId,
                     final Product product,
                     final int quantity) {
        this.id = id;
        this.memberId = memberId;
        this.product = product;
        this.quantity = Quantity.from(quantity);
    }

    public void updateQuantity(final Quantity quantity) {
        this.quantity = quantity;
    }

    public void validateUserHasId(final Long memberId) {
        if (!Objects.equals(this.memberId, memberId)) {
            throw new ShoppingException(ErrorCode.INVALID_CART_ITEM);
        }
    }

    public BigInteger getTotalPrice() {
        return BigInteger.valueOf(product.getPrice().getValue())
                .multiply(BigInteger.valueOf(quantity.getValue()));
    }

    public void validateStock() {
        product.validateSoldOut();
        product.validateCanBuy(quantity.getValue());
    }
}
