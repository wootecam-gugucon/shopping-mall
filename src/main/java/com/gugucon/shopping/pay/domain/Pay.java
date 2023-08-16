package com.gugucon.shopping.pay.domain;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pays")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class Pay extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String orderName;

    private WonMoney price;

    public void validateMoney(final WonMoney price) {
        if (!this.price.equals(price)) {
            throw new RuntimeException();
        }
    }
}
