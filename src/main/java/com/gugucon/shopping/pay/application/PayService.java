package com.gugucon.shopping.pay.application;

import com.gugucon.shopping.pay.domain.Pay;
import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import com.gugucon.shopping.pay.dto.PayValidationRequest;
import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import com.gugucon.shopping.pay.infrastructure.PayValidator;
import com.gugucon.shopping.pay.repository.PayRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PayService {

    private final PayRepository payRepository;
    private final PayValidator payValidator;
    private final OrderIdTranslator orderIdTranslator;

    private final String successUrl;
    private final String failUrl;

    public PayService(final PayRepository payRepository,
                      final PayValidator payValidator,
                      final OrderIdTranslator orderIdTranslator,
                      @Value("${pay.callback.success-url}") final String successUrl,
                      @Value("${pay.callback.fail-url}") final String failUrl) {
        this.payRepository = payRepository;
        this.payValidator = payValidator;
        this.orderIdTranslator = orderIdTranslator;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
    }

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
        return PayResponse.from(payRepository.save(pay), successUrl, failUrl);
    }

    public void validatePay(final PayValidationRequest payValidationRequest) {
        final Long orderId = orderIdTranslator.decode(payValidationRequest.getOrderId());
        final Pay pay = payRepository.findByOrderId(orderId)
                                     .orElseThrow(RuntimeException::new);
        pay.validateMoney(payValidationRequest.getAmount());
        payValidator.validatePayment(payValidationRequest);
    }
}
