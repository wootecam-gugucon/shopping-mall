package shopping.cart.controller.api;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.argumentresolver.annotation.UserId;
import shopping.cart.dto.response.OrderCreateResponse;
import shopping.cart.dto.response.OrderDetailResponse;
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
        final OrderCreateResponse orderCreateResponse = orderService.order(userId);
        return ResponseEntity.created(URI.create("/order/" + orderCreateResponse.getOrderId()))
            .build();
    }

    @GetMapping("/{orderId}/detail")
    @ResponseStatus(HttpStatus.OK)
    public OrderDetailResponse getOrderDetail(@PathVariable Long orderId, @UserId Long userId) {
        return orderService.getOrderDetail(orderId, userId);
    }
}
