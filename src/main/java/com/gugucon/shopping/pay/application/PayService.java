package com.gugucon.shopping.pay.application;

import com.gugucon.shopping.pay.domain.Pay;
import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import com.gugucon.shopping.pay.repository.PayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PayService {

    private final PayRepository payRepository;

    public PayService(PayRepository payRepository) {
        this.payRepository = payRepository;
    }

    @Transactional
    public PayResponse createPay(PayRequest payRequest) {
        // TODO: 결제 금액이 실제 주문 금액과 같은지 확인
        final Pay pay = new Pay(payRequest.getOrderId(), payRequest.getPrice());
        return payRepository.save(pay)
                .toPayResponse();
    }
}
