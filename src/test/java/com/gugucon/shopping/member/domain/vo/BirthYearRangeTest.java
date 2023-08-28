package com.gugucon.shopping.member.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("BirthYearRange 단위 테스트")
class BirthYearRangeTest {

    @ParameterizedTest
    @CsvSource(value = {"19,UNDER_TEENS", 
            "20,EARLY_TWENTIES", 
            "24,MID_TWENTIES", 
            "27,LATE_TWENTIES", 
            "30,THIRTIES", 
            "40,OVER_FORTIES"})
    @DisplayName("생년월일 정보로 나이대 정보를 생성한다.")
    void create(final int age, final BirthYearRange expectedBirthYearRange) {
        // given
        final LocalDate birthDate = LocalDate.of(LocalDate.now().getYear() - age + 1, 1, 1);

        // when
        final BirthYearRange birthYearRange = BirthYearRange.from(birthDate);

        // then
        assertThat(birthYearRange).isEqualTo(expectedBirthYearRange);
    }

    @ParameterizedTest
    @CsvSource(value = {"1,UNDER_TEENS",
            "19,UNDER_TEENS",
            "20,EARLY_TWENTIES",
            "23,EARLY_TWENTIES",
            "24,MID_TWENTIES",
            "26,MID_TWENTIES",
            "27,LATE_TWENTIES",
            "29,LATE_TWENTIES",
            "30,THIRTIES",
            "39,THIRTIES",
            "40,OVER_FORTIES",
            "125,OVER_FORTIES"
    })
    @DisplayName("나이대 정보로 탐색할 생년월일 정보를 생성한다.")
    void match(final int expectedAge, final BirthYearRange birthYearRange) {
        // given
        final LocalDate expectedBirthDate = LocalDate.of(LocalDate.now().getYear() - expectedAge + 1, 1, 1);

        // when
        final LocalDate startDate = birthYearRange.getStartDate();
        final LocalDate endDate = birthYearRange.getEndDate();

        // then
        assertThat(expectedBirthDate).isBetween(startDate, endDate);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 126})
    void createFail_ageNotInRange(final int age) {
        // given
        final LocalDate birthDate = LocalDate.of(LocalDate.now().getYear() - age + 1, 1, 1);

        // when & then
        ShoppingException exception = assertThrows(ShoppingException.class, () -> BirthYearRange.from(birthDate));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_BIRTH_DATE);
    }
}
