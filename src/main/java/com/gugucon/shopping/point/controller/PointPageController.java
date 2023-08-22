package com.gugucon.shopping.point.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PointPageController {

    @GetMapping("/point-charge")
    public String chargePage() {
        return "point-charge";
    }
}
