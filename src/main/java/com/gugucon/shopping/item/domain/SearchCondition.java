package com.gugucon.shopping.item.domain;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.member.domain.vo.Gender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class SearchCondition {

    private static final Sort SORT_BY_RATE = SortKey.RATE.getSort();
    private static final Sort SORT_BY_ORDER_COUNT = SortKey.ORDER_COUNT.getSort();

    private final String keyword;
    private final BirthYearRange birthYearRange;
    private final Gender gender;
    private final Pageable pageable;

    public Sort getSort() {
        return pageable.getSort();
    }

    public void validateSort() {
        if (!SortKey.contains(getSort())) {
            throw new ShoppingException(ErrorCode.INVALID_SORT);
        }
    }

    public void validateKeywordNotBlank() {
        if (keyword.isBlank()) {
            throw new ShoppingException(ErrorCode.EMPTY_INPUT);
        }
    }

    public boolean isSortedByRate() {
        return pageable.getSort().equals(SORT_BY_RATE);
    }

    public boolean isSortedByOrderCount() {
        return pageable.getSort().equals(SORT_BY_ORDER_COUNT);
    }

    public boolean hasValidFilters() {
        return birthYearRange != null && gender != null;
    }
}
