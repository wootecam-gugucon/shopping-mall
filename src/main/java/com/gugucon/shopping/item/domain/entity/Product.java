package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String imageFileName;
    private int stock;
    @Lob
    private String description;
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private WonMoney price;

    public Product(final Long id, final String name, final String imageFileName, final int stock, final String description, final long price) {
        this.id = id;
        this.name = name;
        this.imageFileName = imageFileName;
        this.stock = stock;
        this.description = description;
        this.price = new WonMoney(price);
    }

    public Product(final String name, final String imageFileName, final int stock, final String description, final long price) {
        this(null, name, imageFileName, stock, description, price);
    }
}
