package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String imageFileName;

    @NotNull
    private Integer stock;

    @Lob
    @NotNull
    private String description;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    @Valid
    @NotNull
    private WonMoney price;

    @Builder
    private Product(final Long id,
                    final String name,
                    final String imageFileName,
                    final Integer stock,
                    final String description,
                    final Long price) {
        this.id = id;
        this.name = name;
        this.imageFileName = imageFileName;
        this.stock = stock;
        this.description = description;
        this.price = WonMoney.from(price);
    }
}
