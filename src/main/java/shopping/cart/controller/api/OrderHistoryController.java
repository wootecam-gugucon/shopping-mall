package shopping.cart.controller.api;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.argumentresolver.annotation.UserId;
import shopping.cart.dto.response.OrderDetailResponse;
import shopping.cart.service.OrderService;

@RestController
@RequestMapping("/order-history")
public class OrderHistoryController {

    private final OrderService orderService;

    public OrderHistoryController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDetailResponse> getOrderHistory(@UserId Long userId) {
        return orderService.getOrderHistory(userId);
    }
}
