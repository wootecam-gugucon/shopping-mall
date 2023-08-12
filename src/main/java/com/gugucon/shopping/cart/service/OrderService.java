package com.gugucon.shopping.cart.service;

import com.gugucon.shopping.cart.domain.entity.CartItem;
import com.gugucon.shopping.cart.domain.entity.Order;
import com.gugucon.shopping.cart.domain.vo.ExchangeRate;
import com.gugucon.shopping.cart.dto.response.OrderDetailResponse;
import com.gugucon.shopping.cart.dto.response.OrderHistoryResponse;
import com.gugucon.shopping.cart.dto.response.OrderResponse;
import com.gugucon.shopping.cart.repository.CartItemRepository;
import com.gugucon.shopping.cart.repository.OrderRepository;
import com.gugucon.shopping.cart.service.currency.ExchangeRateProvider;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ExchangeRateProvider exchangeRateProvider;

    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository, ExchangeRateProvider exchangeRateProvider) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.exchangeRateProvider = exchangeRateProvider;
    }

    @Transactional
    public OrderResponse order(final Long userId) {
        final List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        validateNotEmpty(cartItems);
        Order.validateTotalPrice(cartItems);
        final ExchangeRate exchangeRate = exchangeRateProvider.fetchExchangeRate();
        final Order order = Order.from(userId, cartItems, exchangeRate);
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
                .toList();
    }

    private void validateNotEmpty(final List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new ShoppingException(ErrorCode.EMPTY_CART);
        }
    }
}
