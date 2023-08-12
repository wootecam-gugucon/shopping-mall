package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "cart_item")
public class CartItem {

    private static final int DEFAULT_QUANTITY = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Embedded
    private Quantity quantity;

    protected CartItem() {
    }

    public CartItem(final Long id, final Long userId, final Product product,
                    final int quantity) {
        this.id = id;
        this.userId = userId;
        this.product = product;
        this.quantity = new Quantity(quantity);
    }

    public CartItem(final Long userId, final Product product) {
        this(null, userId, product, DEFAULT_QUANTITY);
    }

    public void updateQuantity(final Quantity quantity) {
        this.quantity = quantity;
    }

    public void validateUserHasId(final Long userId) {
        if (!Objects.equals(this.userId, userId)) {
            throw new ShoppingException(ErrorCode.INVALID_CART_ITEM);
        }
    }

    public BigInteger getTotalPrice() {
        return BigInteger.valueOf(product.getPrice().getValue())
                .multiply(BigInteger.valueOf(quantity.getValue()));
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Product getProduct() {
        return product;
    }

    public Quantity getQuantity() {
        return quantity;
    }
}
