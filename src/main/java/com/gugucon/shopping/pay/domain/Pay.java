package com.gugucon.shopping.pay.domain;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.order.domain.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pays")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Pay extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private WonMoney price;

    @Builder
    private Pay(final Long id,
                final Long orderId,
                final Long price) {
        this.id = id;
        this.orderId = orderId;
        this.price = WonMoney.from(price);
    }

    public static Pay from(final Order order) {
        return Pay.builder()
                .orderId(order.getId())
                .price(order.calculateTotalPrice().getValue())
                .build();
    }

    public void validateMoney(final WonMoney price) {
        if (!this.price.equals(price)) {
            throw new ShoppingException(ErrorCode.PAY_FAILED);
        }
    }
}
