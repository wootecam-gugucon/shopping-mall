package com.gugucon.shopping.pay.service;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.pay.domain.Pay;
import com.gugucon.shopping.pay.dto.request.PointPayRequest;
import com.gugucon.shopping.pay.dto.request.TossPayRequest;
import com.gugucon.shopping.pay.dto.response.PayResponse;
import com.gugucon.shopping.pay.dto.request.TossPayFailRequest;
import com.gugucon.shopping.pay.dto.response.TossPayFailResponse;
import com.gugucon.shopping.pay.dto.response.TossPayInfoResponse;
import com.gugucon.shopping.pay.infrastructure.CustomerKeyGenerator;
import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import com.gugucon.shopping.pay.infrastructure.PayValidator;
import com.gugucon.shopping.pay.repository.PayRepository;
import com.gugucon.shopping.point.domain.Point;
import com.gugucon.shopping.point.repository.PointRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PayService {

    private final PayRepository payRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final PayValidator payValidator;
    private final OrderIdTranslator orderIdTranslator;
    private final CustomerKeyGenerator customerKeyGenerator;

    private final String successUrl;
    private final String failUrl;

    public PayService(final PayRepository payRepository,
                      final OrderRepository orderRepository,
                      final CartItemRepository cartItemRepository,
                      final MemberRepository memberRepository,
                      final PointRepository pointRepository,
                      final PayValidator payValidator,
                      final OrderIdTranslator orderIdTranslator,
                      final CustomerKeyGenerator customerKeyGenerator,
                      @Value("${pay.callback.success-url}") final String successUrl,
                      @Value("${pay.callback.fail-url}") final String failUrl) {
        this.payRepository = payRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.memberRepository = memberRepository;
        this.pointRepository = pointRepository;
        this.payValidator = payValidator;
        this.orderIdTranslator = orderIdTranslator;
        this.customerKeyGenerator = customerKeyGenerator;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
    }

    @Transactional
    public PayResponse payByPoint(final PointPayRequest pointPayRequest, final Long memberId) {
        final Long orderId = pointPayRequest.getOrderId();
        final Order order = findUnPayedOrderBy(orderId, memberId);

        final Point point = findPoint(memberId);
        point.use(order.calculateTotalPrice());

        return completePay(memberId, order);
    }

    private Point findPoint(final Long memberId) {
        return pointRepository.findByMemberId(memberId)
                              .orElseThrow(() -> new ShoppingException(ErrorCode.POINT_NOT_ENOUGH));
    }

    public TossPayInfoResponse getTossInfo(final Long orderId, final Long memberId) {
        final Order order = findUnPayedOrderBy(orderId, memberId);
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.UNKNOWN_ERROR));

        return TossPayInfoResponse.from(orderIdTranslator.encode(order),
                                        order,
                                        member,
                                        customerKeyGenerator.generate(member),
                                        successUrl,
                                        failUrl);
    }

    @Transactional
    public PayResponse payByToss(final TossPayRequest tossPayRequest, final Long memberId) {
        final Long orderId = orderIdTranslator.decode(tossPayRequest.getOrderId());
        final Order order = findUnPayedOrderBy(orderId, memberId);

        order.validateMoney(tossPayRequest.getAmount());
        payValidator.validatePayment(tossPayRequest);

        return completePay(memberId, order);
    }

    private PayResponse completePay(final Long memberId, final Order order) {
        cartItemRepository.deleteAllByMemberId(memberId);
        order.pay();
        return PayResponse.from(payRepository.save(Pay.from(order)));
    }

    private Order findUnPayedOrderBy(final Long orderId, final Long memberId) {
        final Order order = orderRepository.findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER));
        order.validateUnPayed();
        return order;
    }

    public TossPayFailResponse decodeOrderId(final TossPayFailRequest tossPayFailRequest) {
        final Long orderId = orderIdTranslator.decode(tossPayFailRequest.getOrderId());
        return TossPayFailResponse.from(orderId);
    }
}
