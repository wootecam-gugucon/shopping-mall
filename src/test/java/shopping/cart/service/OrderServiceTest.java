package shopping.cart.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static shopping.TestUtils.createProduct;
import static shopping.TestUtils.createUser;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shopping.auth.domain.entity.User;
import shopping.auth.repository.UserRepository;
import shopping.cart.domain.entity.CartItem;
import shopping.cart.domain.entity.Order;
import shopping.cart.domain.vo.ExchangeRate;
import shopping.cart.repository.CartItemRepository;
import shopping.cart.repository.OrderItemRepository;
import shopping.cart.repository.OrderRepository;
import shopping.cart.utils.currency.ExchangeRateProvider;

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
        final User user = createUser();
        final CartItem cartItem1 = new CartItem(1L, createUser(), createProduct("치킨", 10000),
            1);
        final CartItem cartItem2 = new CartItem(2L, createUser(), createProduct("피자", 20000),
            2);
        final List<CartItem> cartItems = List.of(cartItem1, cartItem2);
        final ExchangeRate exchangeRate = new ExchangeRate(1300);

        doReturn(user).when(userRepository).getReferenceById(user.getId());
        doReturn(cartItems).when(cartItemRepository).findByUserId(user.getId());
        doReturn(exchangeRate).when(exchangeRateProvider).fetchExchangeRate();
        doReturn(new Order(1L, user, exchangeRate)).when(orderRepository).save(any());

        /* when */
        orderService.order(user.getId());

        /* then */
        verify(orderItemRepository, times(cartItems.size())).save(any());
        verify(cartItemRepository).deleteAll(cartItems);
        verify(orderRepository).save(any());
    }
}
