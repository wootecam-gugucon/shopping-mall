package com.gugucon.shopping.pay.application;

import com.gugucon.shopping.common.domain.vo.WonMoney;
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
import com.gugucon.shopping.pay.dto.*;
import com.gugucon.shopping.pay.infrastructure.OrderIdTranslator;
import com.gugucon.shopping.pay.infrastructure.PayValidator;
import com.gugucon.shopping.pay.repository.PayRepository;
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
    private final PayValidator payValidator;
    private final OrderIdTranslator orderIdTranslator;

    private final String successUrl;
    private final String failUrl;

    public PayService(final PayRepository payRepository,
                      final OrderRepository orderRepository,
                      final ProductRepository productRepository,
                      final CartItemRepository cartItemRepository,
                      final MemberRepository memberRepository,
                      final PayValidator payValidator,
                      final OrderIdTranslator orderIdTranslator,
                      @Value("${pay.callback.success-url}") final String successUrl,
                      @Value("${pay.callback.fail-url}") final String failUrl) {
        this.payRepository = payRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.memberRepository = memberRepository;
        this.payValidator = payValidator;
        this.orderIdTranslator = orderIdTranslator;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
    }

    @Transactional
    public PayCreateResponse createPay(final PayCreateRequest payCreateRequest, final Long memberId) {
        final Long orderId = payCreateRequest.getOrderId();
        final Order order = findOrderBy(memberId, orderId);
        order.validateNotPayed();
        payRepository.findByOrderId(orderId)
                .ifPresent(payRepository::delete);
        final Pay pay = Pay.builder()
                .orderId(orderId)
                .price(order.getTotalPrice())
                .build();
        payRepository.save(pay);
        return PayCreateResponse.from(pay.getId());
    }

    public PayInfoResponse readPayInfo(final Long payId, final Long memberId) {
        final Pay pay = payRepository.findById(payId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_PAY));
        final Order order = findOrderBy(memberId, pay.getOrderId());
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.UNKNOWN_ERROR));
        order.validateNotPayed();
        final String orderName = order.getOrderName();
        return PayInfoResponse.from(member, orderName, orderIdTranslator.encode(order.getId(), orderName),
                                    order.getTotalPrice(), successUrl, failUrl);
    }

    @Transactional
    public PayValidationResponse validatePay(final PayValidationRequest payValidationRequest, final Long memberId) {
        final Long orderId = orderIdTranslator.decode(payValidationRequest.getOrderId());
        final Order order = findOrderBy(memberId, orderId);
        order.validateNotPayed();
        final Pay pay = payRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_PAY));
        pay.validateMoney(WonMoney.from(payValidationRequest.getAmount()));

        order.getOrderItems().forEach(orderItem -> {
            final Product product = productRepository.findById(orderItem.getId())
                    .orElseThrow(() -> new ShoppingException(ErrorCode.UNKNOWN_ERROR));
            product.validateStockIsNotLessThan(orderItem.getQuantity());
            product.decreaseStockBy(orderItem.getQuantity());
        });
        order.pay();

        payValidator.validatePayment(payValidationRequest);
        cartItemRepository.deleteAllByMemberId(memberId);
        return PayValidationResponse.from(orderId);
    }

    private Order findOrderBy(final Long memberId, final Long orderId) {
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER));
        order.validateMemberHasId(memberId);
        return order;
    }
}
