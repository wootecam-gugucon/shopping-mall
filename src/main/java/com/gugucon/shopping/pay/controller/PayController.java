package com.gugucon.shopping.pay.controller;

import com.gugucon.shopping.auth.dto.MemberPrincipal;
import com.gugucon.shopping.pay.dto.request.PointPayRequest;
import com.gugucon.shopping.pay.dto.request.TossPayFailRequest;
import com.gugucon.shopping.pay.dto.request.TossPayRequest;
import com.gugucon.shopping.pay.dto.response.PayResponse;
import com.gugucon.shopping.pay.dto.response.TossPayFailResponse;
import com.gugucon.shopping.pay.dto.response.TossPayInfoResponse;
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

    @PostMapping("/point")
    @ResponseStatus(HttpStatus.OK)
    public PayResponse payByPoint(@RequestBody final PointPayRequest pointPayRequest,
                                  @AuthenticationPrincipal final MemberPrincipal principal) {
        return payService.payByPoint(pointPayRequest, principal);
    }

    @GetMapping("/toss")
    @ResponseStatus(HttpStatus.OK)
    public TossPayInfoResponse getPaymentInfo(@RequestParam final Long orderId,
                                              @AuthenticationPrincipal final MemberPrincipal principal) {
        return payService.getTossInfo(orderId, principal.getId());
    }

    @PostMapping("/toss")
    @ResponseStatus(HttpStatus.OK)
    public PayResponse payByToss(@RequestBody final TossPayRequest tossPayRequest,
                                 @AuthenticationPrincipal final MemberPrincipal principal) {
        return payService.payByToss(tossPayRequest, principal);
    }

    @PostMapping("/fail")
    @ResponseStatus(HttpStatus.OK)
    public TossPayFailResponse failPayment(@RequestBody final TossPayFailRequest tossPayFailRequest) {
        return payService.decodeOrderId(tossPayFailRequest);
    }
}
