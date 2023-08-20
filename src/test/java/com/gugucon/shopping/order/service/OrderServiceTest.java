package com.gugucon.shopping.order.service;

import com.gugucon.shopping.common.domain.vo.Quantity;
import static com.gugucon.shopping.order.domain.entity.Order.OrderStatus.ORDERED;
import static com.gugucon.shopping.utils.DomainUtils.createMember;
import static com.gugucon.shopping.utils.DomainUtils.createProduct;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.repository.OrderRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문한다.")
    void order() {
        /* given */
        final Long memberId = createMember().getId();
        final CartItem cartItem1 = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(createProduct("치킨", 10000))
                .quantity(Quantity.from(1))
                .build();
        final CartItem cartItem2 = CartItem.builder()
                .id(2L)
                .memberId(memberId)
                .product(createProduct("피자", 20000))
                .quantity(Quantity.from(2))
                .build();
        final List<CartItem> cartItems = List.of(cartItem1, cartItem2);

        doReturn(cartItems).when(cartItemRepository).findAllByMemberIdWithProduct(memberId);
        doReturn(Order.builder()
                         .id(1L)
                         .memberId(memberId)
                         .status(ORDERED)
                         .build())
                .when(orderRepository).save(any());

        /* when */
        orderService.order(memberId);

        /* then */
        //verify(cartItemRepository).deleteAll(cartItems);
        verify(orderRepository).save(any());
    }
}
