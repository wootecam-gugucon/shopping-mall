package com.gugucon.shopping.pay.application;

import com.gugucon.shopping.pay.domain.Pay;
import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import com.gugucon.shopping.pay.dto.PayValidationRequest;
import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import com.gugucon.shopping.pay.infrastructure.PayValidator;
import com.gugucon.shopping.pay.repository.PayRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class PayService {

    private final PayRepository payRepository;
    private final PayValidator payValidator;
    private final OrderIdTranslator orderIdTranslator;

    @Transactional
    public PayResponse createPay(final PayRequest payRequest) {
        final Long orderId = payRequest.getOrderId();
        final String orderName = payRequest.getOrderName();
        final Long price = payRequest.getPrice();
        final String encodedOrderId = orderIdTranslator.encode(orderId, orderName);
        final Pay pay = Pay.builder()
                           .orderId(orderId)
                           .encodedOrderId(encodedOrderId)
                           .orderName(orderName)
                           .price(price)
                           .build();
        return PayResponse.of(payRepository.save(pay));
    }

    public void validatePay(final PayValidationRequest payValidationRequest) {
        final Long orderId = orderIdTranslator.decode(payValidationRequest.getOrderId());
        final Pay pay = payRepository.findByOrderId(orderId)
                                     .orElseThrow(RuntimeException::new);
        pay.validateMoney(payValidationRequest.getAmount());
        payValidator.validatePayment(payValidationRequest);
    }
}
