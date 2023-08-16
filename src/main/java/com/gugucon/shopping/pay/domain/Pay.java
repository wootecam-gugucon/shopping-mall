package com.gugucon.shopping.pay.domain;

import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private WonMoney price;

    public void validateMoney(final WonMoney price) {
        if (!this.price.equals(price)) {
            throw new ShoppingException(ErrorCode.PAY_FAILED);
        }
    }
}
