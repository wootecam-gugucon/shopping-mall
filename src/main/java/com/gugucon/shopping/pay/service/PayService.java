package com.gugucon.shopping.pay.service;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.pay.config.TossPayConfiguration;
import com.gugucon.shopping.pay.domain.Pay;
import com.gugucon.shopping.pay.dto.request.PointPayRequest;
import com.gugucon.shopping.pay.dto.request.TossPayFailRequest;
import com.gugucon.shopping.pay.dto.request.TossPayRequest;
import com.gugucon.shopping.pay.dto.response.PayResponse;
import com.gugucon.shopping.pay.dto.response.TossPayFailResponse;
import com.gugucon.shopping.pay.dto.response.TossPayInfoResponse;
import com.gugucon.shopping.pay.infrastructure.TossPayProvider;
import com.gugucon.shopping.pay.repository.PayRepository;
import com.gugucon.shopping.point.domain.Point;
import com.gugucon.shopping.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PayService {

    private final PayRepository payRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final TossPayProvider tossPayProvider;
    private final TossPayConfiguration tossPayConfiguration;

    @Transactional
    public PayResponse payByPoint(final PointPayRequest pointPayRequest, final Long memberId) {
        final Long orderId = pointPayRequest.getOrderId();
        final Order order = findOrderBy(orderId, memberId);

        final Point point = findPointBy(memberId);
        point.use(order.calculateTotalPrice());

        return completePay(memberId, order);
    }

    private Point findPointBy(final Long memberId) {
        return pointRepository.findByMemberId(memberId)
                              .orElseThrow(() -> new ShoppingException(ErrorCode.POINT_NOT_ENOUGH));
    }

    public TossPayInfoResponse getTossInfo(final Long orderId, final Long memberId) {
        final Order order = findOrderBy(orderId, memberId);
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.UNKNOWN_ERROR));

        return TossPayInfoResponse.from(tossPayProvider.encodeOrderId(order.getId(), order.createOrderName()),
                                        order,
                                        member,
                                        tossPayProvider.generateCustomerKey(member.getId()),
                                        tossPayConfiguration.getSuccessUrl(),
                                        tossPayConfiguration.getFailUrl());
    }

    @Transactional
    public PayResponse payByToss(final TossPayRequest tossPayRequest, final Long memberId) {
        final Long orderId = tossPayProvider.decodeOrderId(tossPayRequest.getOrderId());
        final Order order = findOrderBy(orderId, memberId);

        order.validateMoney(tossPayRequest.getAmount());
        tossPayProvider.validatePayment(tossPayRequest);

        return completePay(memberId, order);
    }

    private PayResponse completePay(final Long memberId, final Order order) {
        cartItemRepository.deleteAllByMemberId(memberId);
        order.pay();
        return PayResponse.from(payRepository.save(Pay.from(order)));
    }

    private Order findOrderBy(final Long orderId, final Long memberId) {
        return orderRepository.findByIdAndMemberId(orderId, memberId)
                              .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER));
    }

    public TossPayFailResponse decodeOrderId(final TossPayFailRequest tossPayFailRequest) {
        final Long orderId = tossPayProvider.decodeOrderId(tossPayFailRequest.getOrderId());
        return TossPayFailResponse.from(orderId);
    }
}
