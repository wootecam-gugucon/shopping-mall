package com.gugucon.shopping.item.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RatePageController {

    @GetMapping("/rate/orderItem/{orderItemId}")
    public String ratePopUp(@PathVariable final Long orderItemId, final Model model) {
        model.addAttribute("orderItemId", orderItemId);
        return "rate-popup";
    }
}
