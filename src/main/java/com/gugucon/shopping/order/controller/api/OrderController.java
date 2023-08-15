package com.gugucon.shopping.order.controller.api;

import com.gugucon.shopping.member.argumentresolver.annotation.MemberId;
import com.gugucon.shopping.order.dto.response.OrderDetailResponse;
import com.gugucon.shopping.order.dto.response.OrderResponse;
import com.gugucon.shopping.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> order(@MemberId final Long memberId) {
        final OrderResponse orderResponse = orderService.order(memberId);
        return ResponseEntity.created(URI.create("/order/" + orderResponse.getOrderId())).build();
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDetailResponse getOrderDetail(@PathVariable final Long orderId, @MemberId final Long memberId) {
        return orderService.getOrderDetail(orderId, memberId);
    }
}
