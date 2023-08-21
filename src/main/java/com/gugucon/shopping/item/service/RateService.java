package com.gugucon.shopping.item.service;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.Rate;
import com.gugucon.shopping.item.dto.request.RateCreateRequest;
import com.gugucon.shopping.item.repository.RateRepository;
import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import com.gugucon.shopping.order.domain.entity.OrderItem;
import com.gugucon.shopping.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RateService {

    private static final short MIN_SCORE = 1;
    private static final short MAX_SCORE = 5;

    private final RateRepository rateRepository;
    private final OrderItemRepository orderItemRepository;

    public void createRate(final Long memberId, final RateCreateRequest request) {
        validateScoreRange(request.getScore());

        final OrderItem orderItem = searchOrderItem(memberId, request.getOrderItemId());
        validateDuplicateRate(orderItem.getId());

        final Rate rate = Rate.builder()
            .memberId(memberId)
            .orderItem(orderItem)
            .score(request.getScore())
            .build();

        rateRepository.save(rate);
    }

    private void validateScoreRange(final short rate) {
        if (rate < MIN_SCORE || rate > MAX_SCORE) {
            throw new ShoppingException(ErrorCode.INVALID_RATE);
        }
    }

    private OrderItem searchOrderItem(Long memberId, Long orderItemId) {
        return orderItemRepository.findByOrderIdAndMemberIdAndOrderStatus(memberId, orderItemId, OrderStatus.PAYED)
            .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER_ITEM));
    }

    private void validateDuplicateRate(Long orderItemId) {
        rateRepository.findByOrderItemId(orderItemId)
            .ifPresent(rate -> {
                throw new ShoppingException(ErrorCode.ALREADY_RATED);
            });
    }
}
