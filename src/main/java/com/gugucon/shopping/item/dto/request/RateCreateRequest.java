package com.gugucon.shopping.item.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RateCreateRequest {

    private long orderItemId;

    @Range(min=1, max=5)
    private short score;
}
