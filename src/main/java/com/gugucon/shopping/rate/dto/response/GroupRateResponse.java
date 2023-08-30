package com.gugucon.shopping.rate.dto.response;

import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.member.domain.vo.Gender;
import com.gugucon.shopping.rate.repository.dto.GroupAverageRateDto;
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

    public static GroupRateResponse of(final GroupAverageRateDto groupAverageRateDto, final double averageRate) {
        return new GroupRateResponse(groupAverageRateDto.getGender(),
                                     groupAverageRateDto.getBirthYearRange(),
                                     new RateResponse(groupAverageRateDto.getCount(), averageRate));
    }
}
