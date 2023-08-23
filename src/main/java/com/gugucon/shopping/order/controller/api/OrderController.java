package com.gugucon.shopping.order.controller.api;

import com.gugucon.shopping.order.dto.request.OrderPayRequest;
import com.gugucon.shopping.order.dto.response.OrderDetailResponse;
import com.gugucon.shopping.order.dto.response.OrderResponse;
import com.gugucon.shopping.order.service.OrderService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> order(@AuthenticationPrincipal final Long memberId) {
        final OrderResponse orderResponse = orderService.order(memberId);
        return ResponseEntity.created(URI.create("/order/" + orderResponse.getOrderId())).build();
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDetailResponse getOrderDetail(@PathVariable final Long orderId,
                                              @AuthenticationPrincipal final Long memberId) {
        return orderService.getOrderDetail(orderId, memberId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void requestPay(@RequestBody final OrderPayRequest orderPayRequest, @AuthenticationPrincipal final Long memberId) {
        orderService.requestPay(orderPayRequest, memberId);
    }
}
