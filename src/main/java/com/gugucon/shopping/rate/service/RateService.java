package com.gugucon.shopping.rate.service;

import com.gugucon.shopping.auth.dto.MemberPrincipal;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.item.repository.RateStatRepository;
import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.order.domain.entity.Order.OrderStatus;
import com.gugucon.shopping.order.domain.entity.OrderItem;
import com.gugucon.shopping.order.repository.OrderItemRepository;
import com.gugucon.shopping.rate.domain.entity.Rate;
import com.gugucon.shopping.rate.dto.request.RateCreateRequest;
import com.gugucon.shopping.rate.dto.response.GroupRateResponse;
import com.gugucon.shopping.rate.dto.response.RateDetailResponse;
import com.gugucon.shopping.rate.dto.response.RateResponse;
import com.gugucon.shopping.rate.repository.RateRepository;
import com.gugucon.shopping.rate.repository.dto.AverageRateDto;
import com.gugucon.shopping.rate.repository.dto.GroupAverageRateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RateService {

    private static final short MIN_SCORE = 1;
    private static final short MAX_SCORE = 5;

    private final RateRepository rateRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final RateStatRepository rateStatRepository;

    @Transactional
    public void createRate(final MemberPrincipal principal, final RateCreateRequest request) {
        final short score = request.getScore();
        validateScoreRange(score);

        final OrderItem orderItem = searchOrderItem(principal.getId(), request.getOrderItemId());
        validateDuplicateRate(orderItem.getId());

        final Rate rate = Rate.builder()
                .orderItem(orderItem)
                .score(score)
                .build();

        rateRepository.save(rate);
        rateStatRepository.updateRateStatByScore(score,
                                                 orderItem.getProductId(),
                                                 BirthYearRange.from(principal.getBirthDate()),
                                                 principal.getGender());
    }

    public RateResponse getRates(final Long productId) {
        validateProduct(productId);
        final AverageRateDto rates = rateRepository.findScoresByProductId(productId);
        final double averageRate = calculateAverageOf(rates);
        return new RateResponse(rates.getCount(), roundDownAverage(averageRate));
    }

    public RateDetailResponse getRateDetail(final Long memberId, final Long orderItemId) {
        final int score = rateRepository.findByMemberIdAndOrderItemId(memberId, orderItemId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_RATE));
        return RateDetailResponse.from(score);
    }

    public RateResponse getCustomRate(final Long productId, final MemberPrincipal principal) {
        final BirthYearRange birthYearRange = BirthYearRange.from(principal.getBirthDate());
        final AverageRateDto rates = rateRepository.findScoresByMemberGenderAndMemberBirthYear(productId,
                                                                                               principal.getGender(),
                                                                                               birthYearRange);
        final double averageRate = calculateAverageOf(rates);
        return new RateResponse(rates.getCount(), averageRate);
    }

    public List<GroupRateResponse> getGroupRates(final Long productId) {
        return rateRepository.findAllGroupsOfMemberGenderAndMemberBirthYear(productId).stream()
                .sorted(Comparator.comparing(o -> o.getBirthYearRange().getStartDate(), Comparator.reverseOrder()))
                .map(rateDto -> GroupRateResponse.of(rateDto, calculateAverageOf(rateDto)))
                .toList();
    }

    private double calculateAverageOf(final AverageRateDto averageRateDto) {
        return roundDownAverage((double) averageRateDto.getTotalScore() / averageRateDto.getCount());
    }

    private double calculateAverageOf(final GroupAverageRateDto averageRateDto) {
        return roundDownAverage((double) averageRateDto.getTotalScore() / averageRateDto.getCount());
    }

    private double roundDownAverage(final double average) {
        return Math.floor(average * 100) / 100.0;
    }

    private void validateProduct(final Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ShoppingException(ErrorCode.INVALID_PRODUCT);
        }
    }

    private void validateScoreRange(final short rate) {
        if (rate < MIN_SCORE || rate > MAX_SCORE) {
            throw new ShoppingException(ErrorCode.INVALID_SCORE);
        }
    }

    private OrderItem searchOrderItem(final Long memberId, final Long orderItemId) {
        return orderItemRepository.findByOrderIdAndMemberIdAndOrderStatus(memberId, orderItemId, OrderStatus.COMPLETED)
            .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER_ITEM));
    }

    private void validateDuplicateRate(final Long orderItemId) {
        rateRepository.findByOrderItemId(orderItemId)
            .ifPresent(rate -> {
                throw new ShoppingException(ErrorCode.ALREADY_RATED);
            });
    }
}
