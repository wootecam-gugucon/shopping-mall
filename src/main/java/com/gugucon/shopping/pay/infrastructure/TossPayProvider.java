package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.pay.dto.request.TossPayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TossPayProvider {

    private final PayValidator payValidator;
    private final OrderIdTranslator orderIdTranslator;
    private final CustomerKeyGenerator customerKeyGenerator;

    public String generateCustomerKey(final long value) {
        return customerKeyGenerator.generate(value);
    }

    public String encodeOrderId(final long orderId, final String orderName) {
        return orderIdTranslator.encode(orderId, orderName);
    }

    public long decodeOrderId(final String encodedId) {
        return orderIdTranslator.decode(encodedId);
    }

    public void validatePayment(final TossPayRequest tossPayRequest) {
        payValidator.validatePayment(tossPayRequest);
    }
}
