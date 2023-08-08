package shopping.cart.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.domain.entity.User;
import shopping.auth.repository.UserRepository;
import shopping.cart.domain.entity.CartItem;
import shopping.cart.domain.entity.Order;
import shopping.cart.domain.entity.OrderItem;
import shopping.cart.dto.response.OrderCreateResponse;
import shopping.cart.dto.response.OrderDetailResponse;
import shopping.cart.dto.response.OrderHistoryResponse;
import shopping.cart.repository.CartItemRepository;
import shopping.cart.repository.OrderItemRepository;
import shopping.cart.repository.OrderRepository;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(final UserRepository userRepository, final OrderRepository orderRepository,
        final OrderItemRepository orderItemRepository,
        final CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public OrderCreateResponse order(final Long userId) {
        final User user = userRepository.getReferenceById(userId);
        final List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        Order.validateTotalPrice(cartItems);
        final Order order = Order.of(user);
        cartItems.stream()
            .map(cartItem -> OrderItem.from(cartItem, order))
            .forEach(orderItemRepository::save);
        cartItemRepository.deleteAll(cartItems);
        return OrderCreateResponse.from(orderRepository.save(order));
    }

    public OrderDetailResponse getOrderDetail(final Long orderId, final Long userId) {
        final Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER));
        final User user = userRepository.getReferenceById(userId);

        validateUserHasOrder(user, order);
        return OrderDetailResponse.from(order);
    }

    public List<OrderHistoryResponse> getOrderHistory(final Long userId) {
        final List<Order> orders = orderRepository.findAllByUserIdWithOrderItems(userId,
            Sort.by(Direction.DESC, "id"));
        return orders.stream()
            .map(OrderHistoryResponse::from)
            .collect(Collectors.toUnmodifiableList());
    }

    private void validateUserHasOrder(final User user, final Order order) {
        if (!order.hasUser(user)) {
            throw new ShoppingException(ErrorCode.INVALID_ORDER);
        }
    }
}
