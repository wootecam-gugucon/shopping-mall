package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.WonMoney;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String imageFileName;
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private WonMoney price;

    protected Product() {
    }

    public Product(final Long id, final String name, final String imageFileName,
                   final WonMoney price) {
        this.id = id;
        this.name = name;
        this.imageFileName = imageFileName;
        this.price = price;
    }

    public Product(final String name, final String imageFileName, final WonMoney price) {
        this(null, name, imageFileName, price);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public WonMoney getPrice() {
        return price;
    }
}
