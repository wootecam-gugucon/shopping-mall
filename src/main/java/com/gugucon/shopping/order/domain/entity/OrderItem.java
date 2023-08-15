package com.gugucon.shopping.order.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.item.domain.entity.CartItem;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
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
    private String productName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    @Valid
    @NotNull
    private WonMoney price;

    @NotNull
    private String imageFileName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity"))
    @Valid
    @NotNull
    private Quantity quantity;

    public static OrderItem from(final CartItem cartItem) {
        return new OrderItem(null, cartItem.getProduct().getId(), cartItem.getProduct().getName(),
                             cartItem.getProduct().getPrice(),
                             cartItem.getProduct().getImageFileName(), cartItem.getQuantity());
    }

    public WonMoney getTotalPrice() {
        return price.multiply(quantity);
    }
}
