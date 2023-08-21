package com.gugucon.shopping.item.service;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.Rate;
import com.gugucon.shopping.item.dto.request.RateCreateRequest;
import com.gugucon.shopping.item.repository.RateRepository;
import com.gugucon.shopping.order.domain.entity.OrderItem;
import com.gugucon.shopping.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateService {

    private final RateRepository rateRepository;
    private final OrderItemRepository orderItemRepository;

    public void createRate(final Long memberId, final RateCreateRequest request) {
        final OrderItem orderItem = searchOrderItem(memberId, request.getOrderItemId());

        validateDuplicateRate(orderItem.getId());

        final Rate rate = Rate.builder()
            .memberId(memberId)
            .orderItem(orderItem)
            .score(request.getScore())
            .build();

        rateRepository.save(rate);
    }

    private OrderItem searchOrderItem(Long memberId, Long orderItemId) {
        return orderItemRepository.findByOrderIdAndMemberId(memberId, orderItemId)
            .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER_ITEM));
    }

    private void validateDuplicateRate(Long orderItemId) {
        rateRepository.findByOrderItemId(orderItemId)
            .ifPresent(rate -> {
                throw new ShoppingException(ErrorCode.ALREADY_RATED);
            });
    }
}
