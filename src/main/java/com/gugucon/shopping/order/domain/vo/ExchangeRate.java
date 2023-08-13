package com.gugucon.shopping.order.domain.vo;

import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class ExchangeRate {

    private double value;

    private ExchangeRate(final double value) {
        validatePositive(value);
        this.value = value;
    }

    public static ExchangeRate from(final double value) {
        return new ExchangeRate(value);
    }

    public DollarMoney convert(final WonMoney sourcePrice) {
        validatePositive(value);
        return DollarMoney.from(sourcePrice.getValue() / value);
    }

    private void validatePositive(final double value) {
        if (value <= 0) {
            throw new ShoppingException(ErrorCode.INVALID_EXCHANGE_RATE);
        }
    }
}
