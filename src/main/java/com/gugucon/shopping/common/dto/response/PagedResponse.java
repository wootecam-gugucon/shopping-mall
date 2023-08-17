package com.gugucon.shopping.common.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PagedResponse<T> {

    private List<T> contents;
    private int totalPage;
    private int currentPage;
    private int size;
}
