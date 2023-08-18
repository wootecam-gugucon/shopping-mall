package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.pay.dto.request.PayValidationRequest;

public interface PayValidator {

    void validatePayment(final PayValidationRequest payValidationRequest);
}
