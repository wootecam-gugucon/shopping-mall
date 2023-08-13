package com.gugucon.shopping.order.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.item.domain.entity.CartItem;
import jakarta.persistence.*;
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
    private Long productId;

    private String productName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private WonMoney price;

    private String imageFileName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity"))
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
