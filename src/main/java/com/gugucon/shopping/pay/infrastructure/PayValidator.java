package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.pay.dto.toss.request.TossPayValidationRequest;

public interface PayValidator {

    void validatePayment(final TossPayValidationRequest tossPayValidationRequest);
}
