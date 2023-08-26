package com.gugucon.shopping.order.controller.api;

import com.gugucon.shopping.auth.dto.MemberPrincipal;
import com.gugucon.shopping.common.dto.response.PagedResponse;
import com.gugucon.shopping.order.dto.response.OrderHistoryResponse;
import com.gugucon.shopping.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order-history")
public class OrderHistoryController {

    private final OrderService orderService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PagedResponse<OrderHistoryResponse> getOrderHistory(@SortDefault(sort = "id", direction = Direction.DESC) final Pageable pageable,
                                                               @AuthenticationPrincipal final MemberPrincipal principal) {
        return orderService.getOrderHistory(pageable, principal.getId());
    }
}
