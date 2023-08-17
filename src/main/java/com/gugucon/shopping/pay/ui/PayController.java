package com.gugucon.shopping.pay.ui;

import com.gugucon.shopping.pay.application.PayService;
import com.gugucon.shopping.pay.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public final class PayController {

    private final PayService payService;

    public PayController(final PayService payService) {
        this.payService = payService;
    }

    @GetMapping("/pay")
    public String getPayPage(final @ModelAttribute("request") PayPageRequest request) {
        System.out.println(request);
        return "pay";
    }

    @PostMapping("/api/pay")
    public ResponseEntity<PayResponse> createPayment(@RequestBody final PayRequest payRequest) {
        final PayResponse payResponse = payService.createPay(payRequest);
        return ResponseEntity.ok(payResponse);
    }

    @PostMapping("/api/pay/validate")
    public ResponseEntity<PayValidationResponse> validatePayment(
            @RequestBody final PayValidationRequest payValidationRequest) {
        final PayValidationResponse payValidationResponse = payService.validatePay(payValidationRequest);
        return ResponseEntity.ok().body(payValidationResponse);
    }

    @GetMapping("/pay/loading-popup")
    public String getLoadingPagePopUp(@ModelAttribute final PayValidationRequest payValidationRequest) {
        return "pay-loading-popup";
    }

    @GetMapping("/pay/fail-popup")
    public String getFailPagePopUp(@ModelAttribute final PayFailParameter payFailParameter) {
        return "pay-fail-popup";
    }

    @GetMapping("/pay/success")
    public String getSuccessPage() {
        return "pay-success";
    }

    @GetMapping("/pay/fail")
    public String getFailPage() {
        return "pay-fail";
    }
}
