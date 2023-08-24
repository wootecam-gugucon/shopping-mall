package com.gugucon.shopping.order.service;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.dto.response.OrderDetailResponse;
import com.gugucon.shopping.order.dto.response.OrderHistoryResponse;
import com.gugucon.shopping.order.dto.response.OrderResponse;
import com.gugucon.shopping.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse order(final Long memberId) {
        final List<CartItem> cartItems = cartItemRepository.findAllByMemberIdWithProduct(memberId);

        validateNotEmpty(cartItems);
        Order.validateTotalPrice(cartItems);

        final Order order = Order.from(memberId, cartItems);
        return OrderResponse.from(orderRepository.save(order));
    }

    public OrderDetailResponse getOrderDetail(final Long orderId, final Long memberId) {
        final Order order = orderRepository.findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_ORDER));

        return OrderDetailResponse.from(order);
    }

    public List<OrderHistoryResponse> getOrderHistory(final Long memberId) {
        final List<Order> orders = orderRepository.findAllByMemberIdAndStatusWithOrderItems(memberId,
                                                                                            Order.OrderStatus.PAYED,
                                                                                            Sort.by(Direction.DESC,
                                                                                                    "id"));
        return orders.stream()
                .map(OrderHistoryResponse::from)
                .toList();
    }

    @Transactional
    public void cancelOrder(final Order order) {
        order.getOrderItems()
                .forEach(orderItem -> productRepository.increaseStockByIdAndValue(orderItem.getProductId(),
                                                                                  orderItem.getQuantity().getValue()));
        order.cancel();
    }

    private void validateNotEmpty(final List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new ShoppingException(ErrorCode.EMPTY_CART);
        }
    }
}
