package com.gugucon.shopping.item.domain;

import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Getter
public enum SortKey {

    ORDER_COUNT(DESC, "orderCount"),
    CREATED_AT(DESC, "createdAt"),
    PRICE_DESC(DESC, "price"),
    PRICE_ASC(ASC, "price");

    private final Sort sort;

    SortKey(final Sort.Direction direction, String key) {
        this.sort = Sort.by(direction, key);
    }

    public static boolean contains(final Sort sort) {
        return Arrays.stream(SortKey.values())
                .anyMatch(sortKey -> sortKey.sort.equals(sort));
    }
}
