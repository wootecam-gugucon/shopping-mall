package com.gugucon.shopping.order.service;

import com.gugucon.shopping.order.service.OrderService;
import com.gugucon.shopping.user.repository.UserRepository;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.domain.vo.ExchangeRate;
import com.gugucon.shopping.item.repository.CartItemRepository;
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

import static com.gugucon.shopping.TestUtils.createProduct;
import static com.gugucon.shopping.TestUtils.createUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

    @Mock
    UserRepository userRepository;
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
        final Long userId = createUser().getId();
        final CartItem cartItem1 = new CartItem(1L, userId, createProduct("치킨", 10000),
                1);
        final CartItem cartItem2 = new CartItem(2L, userId, createProduct("피자", 20000),
                2);
        final List<CartItem> cartItems = List.of(cartItem1, cartItem2);
        final ExchangeRate exchangeRate = new ExchangeRate(1300);

        doReturn(cartItems).when(cartItemRepository).findByUserId(userId);
        doReturn(exchangeRate).when(exchangeRateProvider).fetchExchangeRate();
        doReturn(new Order(1L, userId, exchangeRate)).when(orderRepository).save(any());

        /* when */
        orderService.order(userId);

        /* then */
        verify(cartItemRepository).deleteAll(cartItems);
        verify(orderRepository).save(any());
    }
}
