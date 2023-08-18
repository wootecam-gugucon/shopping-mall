package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.vo.Stock;
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
    private Stock stock;

    @NotNull
    @Column(columnDefinition = "TEXT")
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
        this.stock = Stock.from(stock);
        this.description = description;
        this.price = WonMoney.from(price);
    }

    public void validateSoldOut() {
        if (stock.isZero()) {
            throw new ShoppingException(ErrorCode.SOLD_OUT);
        }
    }

    public boolean canReduceStockBy(final int quantity) {
        return stock.isNotLessThan(quantity);
    }
}
