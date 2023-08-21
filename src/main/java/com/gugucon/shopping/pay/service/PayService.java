package com.gugucon.shopping.pay.service;

import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.pay.domain.Pay;
import com.gugucon.shopping.pay.dto.point.request.PointPayRequest;
import com.gugucon.shopping.pay.dto.point.response.PointPayResponse;
import com.gugucon.shopping.pay.dto.toss.request.TossPayCreateRequest;
import com.gugucon.shopping.pay.dto.toss.request.TossPayFailRequest;
import com.gugucon.shopping.pay.dto.toss.request.TossPayValidationRequest;
import com.gugucon.shopping.pay.dto.toss.response.TossPayCreateResponse;
import com.gugucon.shopping.pay.dto.toss.response.TossPayFailResponse;
import com.gugucon.shopping.pay.dto.toss.response.TossPayInfoResponse;
import com.gugucon.shopping.pay.dto.toss.response.TossPayValidationResponse;
import com.gugucon.shopping.pay.infrastructure.CustomerKeyGenerator;
import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import com.gugucon.shopping.pay.infrastructure.PayValidator;
import com.gugucon.shopping.pay.repository.PayRepository;
import com.gugucon.shopping.point.domain.Point;
import com.gugucon.shopping.point.repository.PointRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PayService {

    private final PayRepository payRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
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
                      final ProductRepository productRepository,
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
        this.productRepository = productRepository;
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
    public PointPayResponse createPay(final PointPayRequest pointPayRequest, final Long memberId) {
        final Long orderId = pointPayRequest.getOrderId();
        final Order order = findUnPayedOrderBy(orderId, memberId);

        final Point point = pointRepository.findByMemberId(memberId)
                                           .orElseThrow(() -> new ShoppingException(ErrorCode.POINT_NOT_ENOUGH));
        final Point used = point.use(order.calculateTotalPrice().getValue());
        pointRepository.save(used);

        payRepository.findByOrderId(orderId)
                     .ifPresent(payRepository::delete);

        return PointPayResponse.from(payRepository.save(Pay.from(order)));
    }

    @Transactional
    public TossPayCreateResponse createPay(final TossPayCreateRequest tossPayCreateRequest, final Long memberId) {
        final Long orderId = tossPayCreateRequest.getOrderId();
        final Order order = findUnPayedOrderBy(orderId, memberId);
        payRepository.findByOrderId(orderId)
                .ifPresent(payRepository::delete);

        return TossPayCreateResponse.from(payRepository.save(Pay.from(order)));
    }

    public TossPayInfoResponse readPayInfo(final Long payId, final Long memberId) {
        final Pay pay = payRepository.findById(payId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_PAY));
        final Order order = findUnPayedOrderBy(pay.getOrderId(), memberId);
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.UNKNOWN_ERROR));

        return TossPayInfoResponse.from(orderIdTranslator.encode(order),
                                        order,
                                        pay,
                                        member,
                                        customerKeyGenerator.generate(member),
                                        successUrl,
                                        failUrl);
    }

    @Transactional
    public TossPayValidationResponse validatePay(final TossPayValidationRequest tossPayValidationRequest, final Long memberId) {
        final Long orderId = orderIdTranslator.decode(tossPayValidationRequest.getOrderId());
        final Order order = findUnPayedOrderBy(orderId, memberId);
        final Pay pay = payRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_PAY));

        pay.validateMoney(Money.from(tossPayValidationRequest.getAmount()));
        decreaseStock(order);
        order.pay();
        payValidator.validatePayment(tossPayValidationRequest);

        cartItemRepository.deleteAllByMemberId(memberId);
        return TossPayValidationResponse.from(orderId);
    }

    private void decreaseStock(final Order order) {
        order.getOrderItems().forEach(orderItem -> {
            final Product product = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> new ShoppingException(ErrorCode.UNKNOWN_ERROR));
            product.validateStockIsNotLessThan(orderItem.getQuantity());
            product.decreaseStockBy(orderItem.getQuantity());
        });
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
