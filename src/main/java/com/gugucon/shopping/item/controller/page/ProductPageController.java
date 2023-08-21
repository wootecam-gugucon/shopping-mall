package com.gugucon.shopping.item.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductPageController {

    @GetMapping("/")
    public String readAllProducts() {
        return "index";
    }

    @GetMapping("/search")
    public String searchProducts() {
        return "search";
    }
}
