package com.gugucon.shopping.point.domain;

import com.gugucon.shopping.common.domain.entity.BaseTimeEntity;
import com.gugucon.shopping.common.domain.vo.Money;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "points")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Point extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "point"))
    private Money point;

    public static Point from(final Long memberId) {
        return Point.builder()
                    .memberId(memberId)
                    .point(Money.ZERO)
                    .build();
    }


    public void charge(final Money chargePoint) {
        validatePointPositive(chargePoint);
        point = point.add(chargePoint);
    }

    private void validatePointPositive(final Money chargePoint) {
        if (chargePoint.isNotPositive()) {
            throw new ShoppingException(ErrorCode.POINT_CHARGE_NOT_POSITIVE);
        }
    }

    public void use(final Money usePoint) {
        validatePointEnough(usePoint);
        point = point.subtract(usePoint);
    }

    private void validatePointEnough(final Money usePoint) {
        if (point.isLessThan(usePoint)) {
            throw new ShoppingException(ErrorCode.POINT_NOT_ENOUGH);
        }
    }
}
