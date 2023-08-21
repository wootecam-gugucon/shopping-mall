package com.gugucon.shopping.pay.dto.toss.response;

import com.gugucon.shopping.pay.domain.Pay;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TossPayCreateResponse {

    private Long payId;

    public static TossPayCreateResponse from(final Pay pay) {
        return new TossPayCreateResponse(pay.getId());
    }
}
