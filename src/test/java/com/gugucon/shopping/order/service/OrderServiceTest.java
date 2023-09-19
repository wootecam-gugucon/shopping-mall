package com.gugucon.shopping.order.service;

import static com.gugucon.shopping.utils.DomainUtils.createMemberWithoutId;
import static com.gugucon.shopping.utils.DomainUtils.createProductWithoutId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.Gender;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.order.domain.PayType;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.dto.request.OrderPayRequest;
import com.gugucon.shopping.order.dto.response.OrderResponse;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.utils.DomainUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("OrderService 통합 테스트")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("잘못된 주문 ID로 주문을 조회하면 예외가 발생한다.")
    void findOrderByFail_invalidOrderId() {
        // given
        final Member member = memberRepository.save(DomainUtils.createMember());

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> orderService.findOrderBy(1L, member.getId()));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER);
    }

    @Test
    @DisplayName("잘못된 주문 ID로 결제를 요청하면 예외가 발생한다.")
    void requestPayFail_invalidOrderId() {
        // given
        final Member member = memberRepository.save(DomainUtils.createMember());

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> orderService.requestPay(
                                                                 new OrderPayRequest(1L, PayType.POINT),
                                                                 member.getId()));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 주문한 채로 결제를 요청하면 예외가 발생한다.")
    void requestPayFail_invalidProductId() {
        // given
        final Member member = memberRepository.save(DomainUtils.createMember());
        final Long memberId = member.getId();
        final Order order = orderRepository.save(Order.from(memberId, List.of(DomainUtils.createCartItem())));

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> orderService.requestPay(
                                                                 new OrderPayRequest(order.getId(), PayType.POINT),
                                                                 memberId));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNKNOWN_ERROR);
    }

    @Test
    @DisplayName("재고 차감시 상품 id 순서로 락을 걸어 데드락이 발생하지 않는다")
    void requestPay_deadLock() {
        // given
        final Member memberA = createMember("test1@email.com");
        final Member memberB = createMember("test2@email.com");

        final Product 연어회 = createProduct("연어회");
        final Product 참치회 = createProduct("참치회");

        final OrderResponse orderA = orderItems(memberA.getId(), List.of(연어회, 참치회));
        final OrderResponse orderB = orderItems(memberB.getId(), List.of(참치회, 연어회));

        // when
        final OrderPayRequest requestA = new OrderPayRequest(orderA.getOrderId(), PayType.POINT);
        final OrderPayRequest requestB = new OrderPayRequest(orderB.getOrderId(), PayType.POINT);

        Executors.newSingleThreadExecutor()
            .submit(() -> orderService.requestPay(requestA, memberA.getId()));
        orderService.requestPay(requestB, memberB.getId());
    }

    private Member createMember(final String email) {
        final LocalDate birthDate = LocalDate.of(2023, 07, 07);
        final Member member = createMemberWithoutId(email, birthDate, Gender.FEMALE);
        return memberRepository.save(member);
    }

    private Product createProduct(final String name) {
        return productRepository.save(createProductWithoutId(name, 20000, 5));
    }

    private OrderResponse orderItems(final long memberId, List<Product> products) {
        products.forEach(product -> cartItemRepository.save(DomainUtils.createCartItemWithoutId(memberId, product)));
        return  orderService.order(memberId);
    }
}
