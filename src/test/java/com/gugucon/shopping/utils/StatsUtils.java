package com.gugucon.shopping.utils;

import com.gugucon.shopping.item.domain.entity.OrderStat;
import com.gugucon.shopping.item.domain.entity.RateStat;
import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.member.domain.vo.Gender;
import java.time.LocalDate;

public class StatsUtils {

    public static OrderStat createInitialOrderStat(final Gender gender,
                                                   final LocalDate birthData,
                                                   final long productId) {
        return OrderStat.builder()
                        .productId(productId)
                        .count(0L)
                        .gender(gender)
                        .birthYearRange(BirthYearRange.from(birthData))
                        .build();
    }

    public static RateStat createInitialRateStat(final Gender gender, final LocalDate birthDate, final long productId) {
        return RateStat.builder()
                        .productId(productId)
                        .count(0L)
                        .gender(gender)
                        .birthYearRange(BirthYearRange.from(birthDate))
                        .totalScore(0L)
                        .build();
    }
}
