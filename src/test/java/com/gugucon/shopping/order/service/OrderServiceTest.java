package com.gugucon.shopping.order.service;

import com.gugucon.shopping.common.config.JpaConfig;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.order.domain.PayType;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.dto.request.OrderPayRequest;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.utils.DomainUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(value = {OrderService.class, JpaConfig.class})
@DisplayName("OrderService 통합 테스트")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

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
}
