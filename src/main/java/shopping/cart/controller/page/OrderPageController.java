package shopping.cart.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrderPageController {

    @GetMapping("/order/{orderId}")
    public String getOrderDetailPage(@PathVariable Long orderId) {
        return "order-detail";
    }

    @GetMapping("/order-history")
    public String getOrderHistoryPage() {
        return "order-history";
    }
}
