package com.gugucon.shopping.pay.controller;

import com.gugucon.shopping.pay.dto.PayFailParameter;
import com.gugucon.shopping.pay.dto.PayValidationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pay")
public class PayPageController {


    @GetMapping("/{payId}")
    public String payPage(@PathVariable final Long payId) {
        return "pay";
    }


    @GetMapping("/loading-popup")
    public String getLoadingPagePopUp(@ModelAttribute final PayValidationRequest payValidationRequest) {
        return "pay-loading-popup";
    }

    @GetMapping("/fail-popup")
    public String getFailPagePopUp(@ModelAttribute final PayFailParameter payFailParameter) {
        return "pay-fail-popup";
    }

    @GetMapping("/success")
    public String getSuccessPage() {
        return "pay-success";
    }
}
