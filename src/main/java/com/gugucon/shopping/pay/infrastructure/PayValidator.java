package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.pay.dto.PayValidationRequest;

public interface PayValidator {

    void validatePayment(final PayValidationRequest payValidationRequest);
}
