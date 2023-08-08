package shopping.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrderDetailPageController {

    @GetMapping("/order/{orderId}")
    public String getOrderDetail(@PathVariable Long orderId) {
        return "order-detail";
    }
}
