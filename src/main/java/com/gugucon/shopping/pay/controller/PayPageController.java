package com.gugucon.shopping.pay.controller;

import com.gugucon.shopping.pay.dto.toss.request.TossPayFailRequest;
import com.gugucon.shopping.pay.dto.toss.request.TossPayValidationRequest;
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
    public String getLoadingPagePopUp(@ModelAttribute final TossPayValidationRequest tossPayValidationRequest) {
        return "pay-loading-popup";
    }

    @GetMapping("/fail-popup")
    public String getFailPagePopUp(@ModelAttribute final TossPayFailRequest tossPayFailRequest) {
        return "pay-fail-popup";
    }

    @GetMapping("/success")
    public String getSuccessPage() {
        return "pay-success";
    }
}
