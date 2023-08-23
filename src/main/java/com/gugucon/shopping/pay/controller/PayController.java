package com.gugucon.shopping.pay.controller;

import com.gugucon.shopping.pay.dto.point.request.PointPayRequest;
import com.gugucon.shopping.pay.dto.point.response.PointPayResponse;
import com.gugucon.shopping.pay.dto.toss.request.TossPayCreateRequest;
import com.gugucon.shopping.pay.dto.toss.request.TossPayFailRequest;
import com.gugucon.shopping.pay.dto.toss.request.TossPayValidationRequest;
import com.gugucon.shopping.pay.dto.toss.response.TossPayCreateResponse;
import com.gugucon.shopping.pay.dto.toss.response.TossPayFailResponse;
import com.gugucon.shopping.pay.dto.toss.response.TossPayInfoResponse;
import com.gugucon.shopping.pay.dto.toss.response.TossPayValidationResponse;
import com.gugucon.shopping.pay.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pay")
@RequiredArgsConstructor
public final class PayController {

    private final PayService payService;

    @PutMapping("/point")
    @ResponseStatus(HttpStatus.OK)
    public PointPayResponse createPayment(@RequestBody final PointPayRequest pointPayRequest,
                                          @AuthenticationPrincipal final Long memberId) {
        return payService.payByPoint(pointPayRequest, memberId);
    }

    @GetMapping("/toss")
    @ResponseStatus(HttpStatus.OK)
    public TossPayInfoResponse getPaymentInfo(@RequestParam final Long orderId,
                                              @AuthenticationPrincipal final Long memberId) {
        return payService.readPayInfo(orderId, memberId);
    }

    @PostMapping("/toss")
    @ResponseStatus(HttpStatus.OK)
    public TossPayValidationResponse validatePayment(@RequestBody final TossPayValidationRequest tossPayValidationRequest,
                                                     @AuthenticationPrincipal final Long memberId) {
        return payService.validatePay(tossPayValidationRequest, memberId);
    }

    @PostMapping("/fail")
    @ResponseStatus(HttpStatus.OK)
    public TossPayFailResponse failPayment(@RequestBody final TossPayFailRequest tossPayFailRequest) {
        return payService.decodeOrderId(tossPayFailRequest);
    }
}
