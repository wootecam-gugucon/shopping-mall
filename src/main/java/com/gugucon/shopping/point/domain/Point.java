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

    private Long point;

    public static Point from(final Long memberId) {
        return Point.builder()
                    .memberId(memberId)
                    .point(0L)
                    .build();
    }


    public void charge(final Long chargePoint) {
        validatePointPositive(chargePoint);
        point += chargePoint;
    }

    private void validatePointPositive(final Long chargePoint) {
        if (chargePoint <= 0) {
            throw new ShoppingException(ErrorCode.POINT_CHARGE_NOT_POSITIVE);
        }
    }

    public void use(final Long usePoint) {
        validatePointEnough(usePoint);
        point -= usePoint;
    }

    private void validatePointEnough(final Long usePoint) {
        if (point < usePoint) {
            throw new ShoppingException(ErrorCode.POINT_NOT_ENOUGH);
        }
    }
}
