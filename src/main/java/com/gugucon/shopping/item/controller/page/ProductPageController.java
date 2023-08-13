package com.gugucon.shopping.item.controller.page;

import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductPageController {

    private final ProductService productService;

    @GetMapping("/")
    public String readAllProducts(Model model) {
        List<ProductResponse> products = productService.readAllProducts();
        model.addAttribute("products", products);
        return "index";
    }
}
