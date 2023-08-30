package com.gugucon.shopping.rate.dto.response;

import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.member.domain.vo.Gender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupRateResponse {

    private Gender gender;
    private BirthYearRange birthYearRange;
    private RateResponse rate;

    public static GroupRateResponse of(final Gender gender,
                                       final BirthYearRange birthYearRange,
                                       final long rateCount,
                                       final double averageRate) {
        return new GroupRateResponse(gender, birthYearRange, new RateResponse(rateCount, averageRate));
    }
}
