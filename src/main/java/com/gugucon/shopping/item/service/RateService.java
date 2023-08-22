package com.gugucon.shopping.item.service;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.Rate;
import com.gugucon.shopping.item.dto.request.RateCreateRequest;
import com.gugucon.shopping.item.dto.response.RateDetailResponse;
import com.gugucon.shopping.item.dto.response.RateResponse;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.item.repository.RateRepository;
import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import com.gugucon.shopping.order.domain.entity.OrderItem;
import com.gugucon.shopping.order.repository.OrderItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RateService {

    private static final short MIN_SCORE = 1;
    private static final short MAX_SCORE = 5;

    private final RateRepository rateRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
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

    public RateResponse getRates(final Long productId) {
        validateProduct(productId);
        final List<Rate> rates = rateRepository.findByProductId(productId);
        final double averageRate = calculateAverageRate(rates);
        return new RateResponse(rates.size(), roundDownAverage(averageRate));
    }

    private double calculateAverageRate(final List<Rate> rates) {
        return rates.stream()
            .mapToInt(Rate::getScore)
            .average()
            .orElse(0.0);
    }

    private double roundDownAverage(final double average) {
        return Math.floor(average * 100) / 100.0;
    }

    private void validateProduct(final Long productId) {
        productRepository.findById(productId)
            .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_PRODUCT));
    }

    private void validateScoreRange(final short rate) {
        if (rate < MIN_SCORE || rate > MAX_SCORE) {
            throw new ShoppingException(ErrorCode.INVALID_SCORE);
        }
    }

    private OrderItem searchOrderItem(final Long memberId, final Long orderItemId) {
        return orderItemRepository.findByOrderIdAndMemberIdAndOrderStatus(memberId, orderItemId, OrderStatus.PAYED)
            .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER_ITEM));
    }

    private void validateDuplicateRate(final Long orderItemId) {
        rateRepository.findByOrderItemId(orderItemId)
            .ifPresent(rate -> {
                throw new ShoppingException(ErrorCode.ALREADY_RATED);
            });
    }

    public RateDetailResponse getRateDetail(final Long memberId, final Long orderItemId) {
        final Rate rate = rateRepository.findByMemberIdAndOrderItemId(memberId, orderItemId)
            .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_RATE));
        return new RateDetailResponse(rate.getScore());
    }
}
