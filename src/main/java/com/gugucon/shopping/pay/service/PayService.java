package com.gugucon.shopping.pay.service;

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
import com.gugucon.shopping.pay.infrastructure.CustomerKeyGenerator;
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
    private final CustomerKeyGenerator customerKeyGenerator;

    private final String successUrl;
    private final String failUrl;

    public PayService(final PayRepository payRepository,
                      final OrderRepository orderRepository,
                      final ProductRepository productRepository,
                      final CartItemRepository cartItemRepository,
                      final MemberRepository memberRepository,
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
        this.payValidator = payValidator;
        this.orderIdTranslator = orderIdTranslator;
        this.customerKeyGenerator = customerKeyGenerator;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
    }

    @Transactional
    public PayCreateResponse createPay(final PayCreateRequest payCreateRequest, final Long memberId) {
        final Long orderId = payCreateRequest.getOrderId();
        final Order order = findUnPayedOrderBy(orderId, memberId);
        payRepository.findByOrderId(orderId)
                .ifPresent(payRepository::delete);

        return PayCreateResponse.from(payRepository.save(Pay.from(order)));
    }

    public PayInfoResponse readPayInfo(final Long payId, final Long memberId) {
        final Pay pay = payRepository.findById(payId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_PAY));
        final Order order = findUnPayedOrderBy(pay.getOrderId(), memberId);
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.UNKNOWN_ERROR));

        return PayInfoResponse.from(orderIdTranslator.encode(order),
                                    order,
                                    pay,
                                    member,
                                    customerKeyGenerator.generate(member),
                                    successUrl,
                                    failUrl);
    }

    @Transactional
    public PayValidationResponse validatePay(final PayValidationRequest payValidationRequest, final Long memberId) {
        final Long orderId = orderIdTranslator.decode(payValidationRequest.getOrderId());
        final Order order = findUnPayedOrderBy(orderId, memberId);
        final Pay pay = payRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_PAY));

        pay.validateMoney(WonMoney.from(payValidationRequest.getAmount()));
        decreaseStock(order);
        order.pay();
        payValidator.validatePayment(payValidationRequest);

        cartItemRepository.deleteAllByMemberId(memberId);
        return PayValidationResponse.from(orderId);
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
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER));
        order.validateMemberHasId(memberId);
        order.validateUnPayed();
        return order;
    }
}
