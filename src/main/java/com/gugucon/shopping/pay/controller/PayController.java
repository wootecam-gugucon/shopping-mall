package com.gugucon.shopping.pay.controller;

import com.gugucon.shopping.member.argumentresolver.annotation.MemberId;
import com.gugucon.shopping.pay.dto.*;
import com.gugucon.shopping.pay.service.PayService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public final class PayController {

    private final PayService payService;

    public PayController(final PayService payService) {
        this.payService = payService;
    }

    @PutMapping("/api/v1/pay")
    @ResponseStatus(HttpStatus.OK)
    public PayCreateResponse createPayment(@RequestBody final PayCreateRequest payCreateRequest,
                                           @MemberId Long memberId) {
        return payService.createPay(payCreateRequest, memberId);
    }


    @GetMapping("/api/v1/pay/{payId}")
    @ResponseStatus(HttpStatus.OK)
    public PayInfoResponse getPaymentInfo(@PathVariable Long payId, @MemberId Long memberId) {
        return payService.readPayInfo(payId, memberId);
    }

    @PostMapping("/api/v1/pay/validate")
    @ResponseStatus(HttpStatus.OK)
    public PayValidationResponse validatePayment(@RequestBody final PayValidationRequest payValidationRequest,
                                                 @MemberId Long memberId) {
        return payService.validatePay(payValidationRequest, memberId);
    }
}
