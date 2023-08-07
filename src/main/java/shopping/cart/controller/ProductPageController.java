package shopping.cart.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import shopping.cart.dto.response.ProductResponse;
import shopping.cart.service.ProductService;

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
