package com.gugucon.shopping.pay.ui;

import com.gugucon.shopping.pay.application.PayService;
import com.gugucon.shopping.pay.dto.PayFailParameter;
import com.gugucon.shopping.pay.dto.PayRequest;
import com.gugucon.shopping.pay.dto.PayResponse;
import com.gugucon.shopping.pay.dto.PaySuccessParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<PayResponse> createPayment(@RequestBody PayRequest payRequest) {
        PayResponse payResponse = payService.createPay(payRequest);
        return ResponseEntity.ok(payResponse);
    }

    @GetMapping("/pay/success")
    public String getSuccessPage(@RequestParam("paymentKey") String paymentKey,
                                 @RequestParam("orderId") String orderId,
                                 @RequestParam("amount") int price,
                                 @RequestParam("paymentType") String paymentType) {
        PaySuccessParameter paySuccessParameter = new PaySuccessParameter(paymentKey, orderId, price, paymentType);
        payService.validatePay(paySuccessParameter);
        return "pay-success";
    }

    @GetMapping("/pay/fail")
    public String getFailPage(@RequestParam("code") String errorCode,
                              @RequestParam("messgae") String message,
                              @RequestParam("orderId") String orderId) {
        PayFailParameter payFailParameter = new PayFailParameter(errorCode, message, orderId);
        return "pay-fail";
    }

}
