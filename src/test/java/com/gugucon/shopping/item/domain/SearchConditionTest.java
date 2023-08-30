package com.gugucon.shopping.item.domain;

import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.member.domain.vo.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchConditionTest {

    @Test
    @DisplayName("필터링 조건이 유효한지 확인한다.")
    void hasValidFilters() {
        // given

        // when
        final SearchCondition validSearchCondition = SearchCondition.builder()
                .birthYearRange(BirthYearRange.MID_TWENTIES)
                .gender(Gender.MALE)
                .build();
        final SearchCondition searchConditionWithoutBirthYearRange = SearchCondition.builder()
                .birthYearRange(null)
                .gender(Gender.MALE)
                .build();
        final SearchCondition searchConditionWithoutGender = SearchCondition.builder()
                .birthYearRange(BirthYearRange.MID_TWENTIES)
                .gender(null)
                .build();
        final SearchCondition searchConditionWithoutBirthYearRangeAndGender = SearchCondition.builder()
                .birthYearRange(null)
                .gender(null)
                .build();

        // then
        assertThat(validSearchCondition.hasValidFilters()).isTrue();
        assertThat(searchConditionWithoutBirthYearRange.hasValidFilters()).isFalse();
        assertThat(searchConditionWithoutGender.hasValidFilters()).isFalse();
        assertThat(searchConditionWithoutBirthYearRangeAndGender.hasValidFilters()).isFalse();
    }

}
