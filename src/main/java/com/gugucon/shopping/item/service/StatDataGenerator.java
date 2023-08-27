package com.gugucon.shopping.item.service;

import com.gugucon.shopping.item.domain.entity.OrderStat;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.domain.entity.RateStat;
import com.gugucon.shopping.item.repository.OrderStatRepository;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.item.repository.RateStatRepository;
import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.member.domain.vo.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StatDataGenerator {

    private final ProductRepository productRepository;
    private final RateStatRepository rateStatRepository;
    private final OrderStatRepository orderStatRepository;

    public void generateRateStatData() {
        log.info("start");
        final Set<Long> productIds = productRepository.findAll().stream()
                .map(Product::getId)
                .collect(Collectors.toUnmodifiableSet());

        for (Gender gender : Gender.values()) {
            for (BirthYearRange birthYearRange : BirthYearRange.values()) {
                final List<RateStat> rateStats = rateStatRepository.findAllSimpleRateStatByGenderAndBirthDateBetween(
                                gender,
                                birthYearRange.getStartDate(),
                                birthYearRange.getEndDate()
                        ).stream()
                        .map(simpleRateStatDto ->
                                     RateStat.builder()
                                             .productId(simpleRateStatDto.getProductId())
                                             .birthYearRange(birthYearRange)
                                             .gender(gender)
                                             .totalScore(simpleRateStatDto.getTotalScore())
                                             .count(simpleRateStatDto.getCount())
                                             .build()
                        ).toList();
                log.info("birthYearRange={}, gender={}, rateStatCount={}", birthYearRange, gender, rateStats.size());
                rateStatRepository.saveAll(rateStats);

                final Set<Long> unratedProductIds = new HashSet<>(productIds);
                unratedProductIds.removeAll(rateStats.stream().map(RateStat::getProductId).collect(Collectors.toSet()));
                log.info("birthYearRange={}, gender={}, unratedProductCount={}",
                         birthYearRange,
                         gender,
                         unratedProductIds.size());
                final List<RateStat> rateStatsOfUnratedProducts = unratedProductIds.stream().map(
                        id -> RateStat.builder()
                                .productId(id)
                                .birthYearRange(birthYearRange)
                                .gender(gender)
                                .totalScore(0L)
                                .count(0L)
                                .build()
                ).toList();
                rateStatRepository.saveAll(rateStatsOfUnratedProducts);
                log.info("totalCount={}", rateStats.size() + unratedProductIds.size());
            }
        }
        log.info("end");
    }

    public void generateOrderStatData() {
        log.info("start");
        final Set<Long> productIds = productRepository.findAll().stream()
                .map(Product::getId)
                .collect(Collectors.toUnmodifiableSet());

        for (Gender gender : Gender.values()) {
            for (BirthYearRange birthYearRange : BirthYearRange.values()) {
                final List<OrderStat> orderStats = orderStatRepository.findAllSimpleOrderStatByGenderAndBirthDateBetween(
                                gender,
                                birthYearRange.getStartDate(),
                                birthYearRange.getEndDate()
                        ).stream()
                        .map(simpleOrderStatDto ->
                                     OrderStat.builder()
                                             .productId(simpleOrderStatDto.getProductId())
                                             .birthYearRange(birthYearRange)
                                             .gender(gender)
                                             .count(simpleOrderStatDto.getCount())
                                             .build()
                        ).toList();
                log.info("birthYearRange={}, gender={}, orderStatCount={}", birthYearRange, gender, orderStats.size());
                orderStatRepository.saveAll(orderStats);

                final Set<Long> unorderedProductIds = new HashSet<>(productIds);
                unorderedProductIds.removeAll(
                        orderStats.stream().map(OrderStat::getProductId).collect(Collectors.toSet()));
                log.info("birthYearRange={}, gender={}, unorderedProductCount={}",
                         birthYearRange,
                         gender,
                         unorderedProductIds.size());
                final List<OrderStat> orderStatsOfUnorderedProducts = unorderedProductIds.stream().map(
                        id -> OrderStat.builder()
                                .productId(id)
                                .birthYearRange(birthYearRange)
                                .gender(gender)
                                .count(0L)
                                .build()
                ).toList();
                orderStatRepository.saveAll(orderStatsOfUnorderedProducts);
                log.info("totalCount={}", orderStats.size() + unorderedProductIds.size());
            }
        }
        log.info("end");
    }
}
