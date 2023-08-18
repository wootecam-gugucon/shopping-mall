package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "products")
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
    @AttributeOverride(name = "value", column = @Column(name = "stock"))
    private Quantity stock;

    @Lob
    @NotNull
    private String description;

    @Embedded
    @Valid
    @NotNull
    @AttributeOverride(name = "value", column = @Column(name = "price"))
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
        this.stock = Quantity.from(stock);
        this.description = description;
        this.price = WonMoney.from(price);
    }

    public void validateSoldOut() {
        if (stock.isZero()) {
            throw new ShoppingException(ErrorCode.SOLD_OUT);
        }
    }

    public void validateStockIsNotLessThan(final Quantity quantity) {
        if (stock.isLessThan(quantity)) {
            throw new ShoppingException(ErrorCode.STOCK_NOT_ENOUGH);
        }
    }

    public void decreaseStockBy(final Quantity other) {
        stock = stock.decreaseBy(other);
    }

    public boolean canReduceStockBy(final Quantity quantity) {
        return !stock.isLessThan(quantity);
    }
}
