package com.gugucon.shopping.order.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.CartItem;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    @NotNull
    private Long productId;

    @NotNull
    private String name;

    @Embedded
    @Valid
    @NotNull
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private Money price;

    @NotNull
    private String imageFileName;

    @Embedded
    @Valid
    @NotNull
    @AttributeOverride(name = "value", column = @Column(name = "quantity"))
    private Quantity quantity;

    public static OrderItem from(final CartItem cartItem) {
        validateSoldOut(cartItem);
        validateQuantity(cartItem);

        return new OrderItem(null,
                             cartItem.getProduct().getId(),
                             cartItem.getProduct().getName(),
                             cartItem.getProduct().getPrice(),
                             cartItem.getProduct().getImageFileName(),
                             cartItem.getQuantity());
    }

    private static void validateSoldOut(final CartItem cartItem) {
        cartItem.getProduct().validateSoldOut();
    }

    private static void validateQuantity(final CartItem cartItem) {
        if (!cartItem.isAvailableQuantity()) {
            throw new ShoppingException(ErrorCode.LACK_OF_STOCK);
        }
    }

    public Money getTotalPrice() {
        return price.multiply(quantity);
    }
}
