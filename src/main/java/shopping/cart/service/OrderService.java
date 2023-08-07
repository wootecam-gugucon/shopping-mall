package shopping.cart.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.domain.entity.User;
import shopping.auth.repository.UserRepository;
import shopping.cart.domain.entity.CartItem;
import shopping.cart.domain.entity.Order;
import shopping.cart.domain.entity.OrderItem;
import shopping.cart.dto.response.OrderResponse;
import shopping.cart.repository.CartItemRepository;
import shopping.cart.repository.OrderItemRepository;
import shopping.cart.repository.OrderRepository;

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
    public OrderResponse order(final Long userId) {
        final User user = userRepository.getReferenceById(userId);
        final List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        Order.validateTotalPrice(cartItems);
        final Order order = Order.of(user);
        cartItems.stream()
            .map(cartItem -> OrderItem.from(cartItem, order))
            .forEach(orderItemRepository::save);
        return OrderResponse.from(orderRepository.save(order));
    }
}
