package com.gugucon.shopping.pay.ui;

import com.gugucon.shopping.pay.application.PayService;
import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public final class PayController {

    private final PayService payService;

    public PayController(PayService payService) {
        this.payService = payService;
    }

    @GetMapping("/pay")
    public String getPayPage() {
        return "pay";
    }

    @PostMapping("/api/pay")
    public ResponseEntity<PayResponse> validatePayment(@RequestBody PayRequest payRequest) {
        PayResponse payResponse = payService.createPay(payRequest);
        return ResponseEntity.ok(payResponse);
    }
}
