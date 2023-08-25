package com.gugucon.shopping.member.domain.vo;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import java.time.LocalDate;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BirthYearRange {
    UNDER_TEENS(0, 19),
    EARLY_TWENTIES(20, 23),
    MID_TWENTIES(24, 26),
    LATE_TWENTIES(27, 29),
    THIRTIES(30, 39),
    OVER_FORTIES(40, 125);

    private final int firstAge;
    private final int lastAge;

    public static BirthYearRange from(final LocalDate birthDate) {
        final int age = LocalDate.now().getYear() - birthDate.getYear() + 1;

        return Arrays.stream(BirthYearRange.values())
                     .filter(range -> range.firstAge <= age && range.lastAge >= age)
                     .findAny()
                     .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_BIRTH_DATE));
    }

    public LocalDate getStartDate() {
        return LocalDate.of(LocalDate.now().getYear() - lastAge + 1, 1, 1);
    }

    public LocalDate getEndDate() {
        return LocalDate.of(LocalDate.now().getYear() - firstAge + 1, 12, 31)
                        .plusDays(1);
    }
}
