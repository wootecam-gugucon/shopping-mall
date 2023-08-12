package com.gugucon.shopping.cart.domain.entity;

import com.gugucon.shopping.cart.domain.vo.Quantity;
import com.gugucon.shopping.cart.domain.vo.WonMoney;

import jakarta.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id")
    private Long productId;
    private String productName;
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private WonMoney price;
    private String imageFileName;
    @Embedded
    private Quantity quantity;

    protected OrderItem() {
    }

    public OrderItem(Long id, Long productId, String productName, WonMoney price, String imageFileName, Quantity quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageFileName = imageFileName;
        this.quantity = quantity;
    }

    public static OrderItem from(final CartItem cartItem) {
        return new OrderItem(null, cartItem.getProduct().getId(), cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getProduct().getImageFileName(), cartItem.getQuantity());
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public WonMoney getPrice() {
        return price;
    }

    public WonMoney getTotalPrice() {
        return price.multiply(quantity);
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public Quantity getQuantity() {
        return quantity;
    }
}
