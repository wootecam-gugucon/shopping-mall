package com.gugucon.shopping.pay.controller;

import com.gugucon.shopping.pay.dto.request.PayCreateRequest;
import com.gugucon.shopping.pay.dto.request.PayValidationRequest;
import com.gugucon.shopping.pay.dto.response.PayCreateResponse;
import com.gugucon.shopping.pay.dto.response.PayInfoResponse;
import com.gugucon.shopping.pay.dto.response.PayValidationResponse;
import com.gugucon.shopping.pay.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public final class PayController {

    private final PayService payService;

    @PutMapping("/api/v1/pay")
    @ResponseStatus(HttpStatus.OK)
    public PayCreateResponse createPayment(@RequestBody final PayCreateRequest payCreateRequest,
                                           @AuthenticationPrincipal final Long memberId) {
        return payService.createPay(payCreateRequest, memberId);
    }


    @GetMapping("/api/v1/pay/{payId}")
    @ResponseStatus(HttpStatus.OK)
    public PayInfoResponse getPaymentInfo(@PathVariable final Long payId,
                                          @AuthenticationPrincipal final Long memberId) {
        return payService.readPayInfo(payId, memberId);
    }

    @PostMapping("/api/v1/pay/validate")
    @ResponseStatus(HttpStatus.OK)
    public PayValidationResponse validatePayment(@RequestBody final PayValidationRequest payValidationRequest,
                                                 @AuthenticationPrincipal final Long memberId) {
        return payService.validatePay(payValidationRequest, memberId);
    }
}
