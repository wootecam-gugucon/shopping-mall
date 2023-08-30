package com.gugucon.shopping.rate.repository.dto;

import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.member.domain.vo.Gender;
import lombok.Getter;

@Getter
public class GroupAverageRateDto {

    private final Gender gender;
    private final BirthYearRange birthYearRange;
    private final Long count;
    private final Long totalScore;

    public GroupAverageRateDto(final Gender gender,
                               final BirthYearRange birthYearRange,
                               final Long count,
                               final Long totalScore) {
        this.gender = gender;
        this.birthYearRange = birthYearRange;
        this.count = count;
        this.totalScore = totalScore;
    }
}
