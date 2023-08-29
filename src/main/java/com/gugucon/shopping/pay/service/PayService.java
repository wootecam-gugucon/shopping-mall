package com.gugucon.shopping.pay.service;

import com.gugucon.shopping.auth.dto.MemberPrincipal;
import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.item.repository.OrderStatRepository;
import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.domain.entity.OrderItem;
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
    private final PointRepository pointRepository;
    private final TossPayProvider tossPayProvider;
    private final TossPayConfiguration tossPayConfiguration;
    private final OrderStatRepository orderStatRepository;

    @Transactional
    public PayResponse payByPoint(final PointPayRequest pointPayRequest, final MemberPrincipal principal) {
        final Long orderId = pointPayRequest.getOrderId();
        final Long memberId = principal.getId();
        final Order order = findOrderByExclusively(orderId, memberId);
        order.validateCanceled();

        final Point point = findPointBy(memberId);
        point.use(order.calculateTotalPrice());
        order.getOrderItems().forEach(orderItem -> updateOrderStatBy(principal, orderItem));

        return completePay(memberId, order);
    }

    private Point findPointBy(final Long memberId) {
        return pointRepository.findByMemberId(memberId)
                              .orElseThrow(() -> new ShoppingException(ErrorCode.POINT_NOT_ENOUGH));
    }

    public TossPayInfoResponse getTossInfo(final Long orderId, final Long memberId) {
        final Order order = findOrderBy(orderId, memberId);
        order.validateCanceled();

        return TossPayInfoResponse.from(tossPayProvider.encodeOrderId(order.getId(), order.createOrderName()),
                                        order,
                                        tossPayProvider.generateCustomerKey(memberId),
                                        tossPayConfiguration.getSuccessUrl(),
                                        tossPayConfiguration.getFailUrl());
    }

    @Transactional
    public PayResponse payByToss(final TossPayRequest tossPayRequest, final MemberPrincipal principal) {
        final Long orderId = tossPayProvider.decodeOrderId(tossPayRequest.getOrderId());
        final Long memberId = principal.getId();
        final Order order = findOrderByExclusively(orderId, memberId);

        order.validateCanceled();
        order.validateMoney(Money.from(tossPayRequest.getAmount()));
        order.getOrderItems().forEach(orderItem -> updateOrderStatBy(principal, orderItem));
        tossPayProvider.validatePayment(tossPayRequest);

        return completePay(memberId, order);
    }

    private PayResponse completePay(final Long memberId, final Order order) {
        cartItemRepository.deleteAllByMemberId(memberId);
        order.completePay();
        return PayResponse.from(payRepository.save(Pay.from(order)));
    }

    private void updateOrderStatBy(final MemberPrincipal principal, final OrderItem orderItem) {
        orderStatRepository.updateOrderStatByCount(orderItem.getQuantity().getValue(),
                                                   orderItem.getProductId(),
                                                   BirthYearRange.from(principal.getBirthDate()),
                                                   principal.getGender());
    }

    private Order findOrderByExclusively(final Long orderId, final Long memberId) {
        return orderRepository.findByIdAndMemberIdExclusively(orderId, memberId)
            .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER));
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
