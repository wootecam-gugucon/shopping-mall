package com.gugucon.shopping.item.controller.page;

import java.util.List;

import com.gugucon.shopping.item.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.gugucon.shopping.item.dto.response.ProductResponse;

@Controller
public class ProductPageController {

    private final ProductService productService;

    public ProductPageController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String findAllProducts(Model model) {
        List<ProductResponse> products = productService.findAllProducts();
        model.addAttribute("products", products);
        return "index";
    }
}
