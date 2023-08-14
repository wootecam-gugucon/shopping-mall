package com.gugucon.shopping.pay.application;

import com.gugucon.shopping.pay.domain.Pay;
import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import com.gugucon.shopping.pay.dto.PaySuccessParameter;
import com.gugucon.shopping.pay.infrastructure.TossPayValidator;
import com.gugucon.shopping.pay.repository.PayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PayService {

    private final PayRepository payRepository;
    private final TossPayValidator tossPayValidator;

    public PayService(final PayRepository payRepository, final TossPayValidator tossPayValidator) {
        this.payRepository = payRepository;
        this.tossPayValidator = tossPayValidator;
    }

    @Transactional
    public PayResponse createPay(final PayRequest payRequest) {
        // TODO: 결제 금액이 실제 주문 금액과 같은지 확인
        final Pay pay = new Pay(payRequest.getOrderId(), payRequest.getOrderName(), payRequest.getPrice());
        return payRepository.save(pay)
                .toPayResponse();
    }

    public void validatePay(final PaySuccessParameter paySuccessParameter) {
        final Pay pay = payRepository.findByEncodedOrderId(paySuccessParameter.getOrderId())
                               .orElseThrow(RuntimeException::new);
        pay.validateMoney(paySuccessParameter.getPrice());
        tossPayValidator.validatePayment(paySuccessParameter);
    }
}
