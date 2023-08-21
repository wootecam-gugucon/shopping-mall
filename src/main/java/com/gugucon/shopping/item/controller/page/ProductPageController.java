package com.gugucon.shopping.item.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductPageController {

    @GetMapping("/")
    public String readAllProducts() {
        return "index";
    }

    @GetMapping("/product/{productId}")
    public String productDetailPage(@PathVariable final String productId) {
        return "product-detail";
    }
}
