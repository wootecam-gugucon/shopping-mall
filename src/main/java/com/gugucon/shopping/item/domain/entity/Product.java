package com.gugucon.shopping.item.domain.entity;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
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
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String description;

    @Embedded
    @Valid
    @NotNull
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private Money price;

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
