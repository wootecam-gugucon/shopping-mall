package com.gugucon.shopping.pay.ui;

import com.gugucon.shopping.pay.application.PayService;
import com.gugucon.shopping.pay.dto.PayFailParameter;
import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import com.gugucon.shopping.pay.dto.PaySuccessParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public final class PayController {

    private final PayService payService;

    public PayController(final PayService payService) {
        this.payService = payService;
    }

    @GetMapping("/pay")
    public String getPayPage() {
        return "pay";
    }

    @PostMapping("/api/pay")
    public ResponseEntity<PayResponse> createPayment(@RequestBody final PayRequest payRequest) {
        final PayResponse payResponse = payService.createPay(payRequest);
        return ResponseEntity.ok(payResponse);
    }

    @GetMapping("/pay/success")
    public String getSuccessPage(@ModelAttribute final PaySuccessParameter paySuccessParameter) {
        payService.validatePay(paySuccessParameter);
        return "pay-success";
    }

    @GetMapping("/pay/fail")
    public String getFailPage(@ModelAttribute final PayFailParameter payFailParameter) {
        return "pay-fail";
    }

}
