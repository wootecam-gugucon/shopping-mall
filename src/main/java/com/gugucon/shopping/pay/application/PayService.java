package com.gugucon.shopping.pay.application;

import com.gugucon.shopping.pay.domain.Pay;
import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import com.gugucon.shopping.pay.dto.PaySuccessParameter;
import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import com.gugucon.shopping.pay.infrastructure.TossPayValidator;
import com.gugucon.shopping.pay.repository.PayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PayService {

    private final PayRepository payRepository;
    private final TossPayValidator tossPayValidator;
    private final OrderIdTranslator orderIdTranslator;

    public PayService(final PayRepository payRepository,
                      final TossPayValidator tossPayValidator,
                      final OrderIdTranslator orderIdTranslator) {
        this.payRepository = payRepository;
        this.tossPayValidator = tossPayValidator;
        this.orderIdTranslator = orderIdTranslator;
    }

    @Transactional
    public PayResponse createPay(final PayRequest payRequest) {
        // TODO: 결제 금액이 실제 주문 금액과 같은지 확인
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

    public void validatePay(final PaySuccessParameter paySuccessParameter) {
        Long orderId = orderIdTranslator.decode(paySuccessParameter.getOrderId());
        final Pay pay = payRepository.findByOrderId(orderId)
                               .orElseThrow(RuntimeException::new);
        pay.validateMoney(paySuccessParameter.getAmount());
        tossPayValidator.validatePayment(paySuccessParameter);
    }
}
