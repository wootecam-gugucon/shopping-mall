package com.gugucon.shopping.order.scheduler;

import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.gugucon.shopping.order.domain.entity.Order.OrderStatus.CANCELED;
import static com.gugucon.shopping.order.domain.entity.Order.OrderStatus.COMPLETED;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancelService {

    private static final Duration CANCEL_INTERVAL = Duration.ofMinutes(30);
    private static final LocalDateTime DEFAULT_SCAN_START_TIME = LocalDateTime.of(2023, 1, 1, 0, 0);

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private LocalDateTime lastScanTime;

    @Transactional
    public void cancelIncompleteOrders() {
        log.info("cancelling started");
        final LocalDateTime scanStartTime = lastScanTime == null ? DEFAULT_SCAN_START_TIME : lastScanTime;
        final LocalDateTime scanEndTime = LocalDateTime.now().minus(CANCEL_INTERVAL);
        if (scanStartTime.isAfter(scanEndTime)) {
            log.warn("scan start time is after scan end time. schedule is cancelled.");
            return;
        }
        final List<Order> incompleteOrders = orderRepository.findAllByStatusNotInAndLastModifiedAtBetweenWithOrderItems(
                List.of(CANCELED, COMPLETED),
                scanStartTime,
                scanEndTime);
        log.info("number of incomplete orders={}", incompleteOrders.size());
        incompleteOrders.forEach(orderService::cancelOrder);
        lastScanTime = scanEndTime;
        log.info("cancelling ended");
    }
}
