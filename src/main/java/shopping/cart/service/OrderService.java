package shopping.cart.service;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.domain.entity.User;
import shopping.auth.repository.UserRepository;
import shopping.cart.domain.entity.CartItem;
import shopping.cart.domain.entity.Order;
import shopping.cart.domain.vo.ExchangeRate;
import shopping.cart.dto.response.OrderDetailResponse;
import shopping.cart.dto.response.OrderHistoryResponse;
import shopping.cart.dto.response.OrderResponse;
import shopping.cart.repository.CartItemRepository;
import shopping.cart.repository.OrderRepository;
import shopping.cart.service.currency.ExchangeRateProvider;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ExchangeRateProvider exchangeRateProvider;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, CartItemRepository cartItemRepository, ExchangeRateProvider exchangeRateProvider) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.exchangeRateProvider = exchangeRateProvider;
    }

    @Transactional
    public OrderResponse order(final Long userId) {
        final User user = userRepository.getReferenceById(userId);
        final List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        validateNotEmpty(cartItems);
        Order.validateTotalPrice(cartItems);
        final ExchangeRate exchangeRate = exchangeRateProvider.fetchExchangeRate();
        final Order order = Order.from(user, cartItems, exchangeRate);
        cartItemRepository.deleteAll(cartItems);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(final Long orderId, final Long userId) {
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER));

        order.validateUserHasId(userId);
        return OrderDetailResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getOrderHistory(final Long userId) {
        final List<Order> orders = orderRepository.findAllByUserIdWithOrderItems(userId,
                Sort.by(Direction.DESC, "id"));
        return orders.stream()
                .map(OrderHistoryResponse::from)
                .collect(Collectors.toUnmodifiableList());
    }

    private void validateNotEmpty(final List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new ShoppingException(ErrorCode.EMPTY_CART);
        }
    }
}
