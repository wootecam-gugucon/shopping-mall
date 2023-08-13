package com.gugucon.shopping.order.service;

import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.domain.vo.ExchangeRate;
import com.gugucon.shopping.order.repository.OrderItemRepository;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.order.service.currency.ExchangeRateProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.gugucon.shopping.TestUtils.createMember;
import static com.gugucon.shopping.TestUtils.createProduct;
import static com.gugucon.shopping.order.domain.entity.Order.OrderStatus.ORDERED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderItemRepository orderItemRepository;
    @Mock
    CartItemRepository cartItemRepository;
    @Mock
    ExchangeRateProvider exchangeRateProvider;
    @InjectMocks
    OrderService orderService;

    @Test
    @DisplayName("주문에 성공한다.")
    void orderSuccess() {
        /* given */
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10000))
                .quantity(1)
                .build();
        final CartItem cartItem2 = CartItem.builder()
                .id(2L)
                .memberId(memberId)
                .product(createProduct("피자", 20000))
                .quantity(2)
                .build();
        final List<CartItem> cartItems = List.of(cartItem1, cartItem2);
        final ExchangeRate exchangeRate = ExchangeRate.from(1300);

        doReturn(cartItems).when(cartItemRepository).findByMemberId(memberId);
        doReturn(exchangeRate).when(exchangeRateProvider).fetchExchangeRate();
        doReturn(Order.builder()
                .id(1L)
                .memberId(memberId)
                .status(ORDERED)
                .exchangeRate(exchangeRate)
                .build())
                .when(orderRepository).save(any());

        /* when */
        orderService.order(memberId);

        /* then */
        verify(cartItemRepository).deleteAll(cartItems);
        verify(orderRepository).save(any());
    }
}
