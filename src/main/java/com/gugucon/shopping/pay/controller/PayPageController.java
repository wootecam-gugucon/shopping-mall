package com.gugucon.shopping.pay.controller;

import com.gugucon.shopping.pay.dto.PayFailParameter;
import com.gugucon.shopping.pay.dto.PayValidationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PayPageController {


    @GetMapping("/pay/{payId}")
    public String payPage(@PathVariable final Long payId) {
        return "pay";
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
}
