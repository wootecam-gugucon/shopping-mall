package com.gugucon.shopping.utils;

import com.gugucon.shopping.item.domain.entity.OrderStat;
import com.gugucon.shopping.item.domain.entity.RateStat;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.order.domain.entity.OrderItem;
import com.gugucon.shopping.rate.domain.entity.Rate;
import java.util.List;

public class StatsUtils {

    public static List<OrderStat> createSingleOrderStat(final Member member, final List<OrderItem> orderItems) {
        return orderItems.stream()
            .map(oi -> OrderStat.builder()
                .productId(oi.getProductId())
                .count(Long.valueOf(oi.getQuantity().getValue()))
                .gender(member.getGender())
                .birthYearRange(BirthYearRange.from(member.getBirthDate()))
                .build())
            .toList();
    }

    public static RateStat createRateStat(final Member member, final Rate rate) {
        return RateStat.builder().build();
    }
}
