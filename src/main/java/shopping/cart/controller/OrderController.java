package shopping.cart.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.argumentresolver.annotation.UserId;
import shopping.cart.dto.response.OrderResponse;
import shopping.cart.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Void> order(@UserId Long userId) {
        final OrderResponse orderResponse = orderService.order(userId);
        return ResponseEntity.created(URI.create("/order/" + orderResponse.getOrderId())).build();
    }
}
