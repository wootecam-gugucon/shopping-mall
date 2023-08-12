package com.gugucon.shopping.order.controller.api;

import java.util.List;

import com.gugucon.shopping.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.gugucon.shopping.user.argumentresolver.annotation.UserId;
import com.gugucon.shopping.order.dto.response.OrderHistoryResponse;

@RestController
@RequestMapping("/api/v1/order-history")
public class OrderHistoryController {

    private final OrderService orderService;

    public OrderHistoryController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderHistoryResponse> getOrderHistory(@UserId Long userId) {
        return orderService.getOrderHistory(userId);
    }
}
