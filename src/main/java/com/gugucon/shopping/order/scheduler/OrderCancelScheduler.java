package com.gugucon.shopping.order.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OrderCancelScheduler {

    private final OrderCancelService orderCancelService;

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    public void trigger() {
        orderCancelService.cancelIncompleteOrders();
    }
}
