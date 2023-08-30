package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.pay.dto.request.TossPayRequest;

public interface PayValidator {

    void validatePayment(final TossPayRequest tossPayRequest);
}
