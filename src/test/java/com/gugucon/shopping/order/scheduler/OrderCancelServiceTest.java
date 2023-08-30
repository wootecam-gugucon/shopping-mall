package com.gugucon.shopping.order.scheduler;

import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.order.domain.PayType;
import com.gugucon.shopping.order.domain.entity.Order;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.gugucon.shopping.utils.DomainUtils.createCartItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class OrderCancelServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderCancelService orderCancelService;

    @Test
    @DisplayName("주문을 취소하는 데 실패하면 마지막 스캔 시점이 업데이트되지 않는다.")
    void cancelIncompleteOrdersFail_doNotUpdateLastScanTime() {
        // given
        final LocalDateTime lastScanTime = LocalDateTime.now().minusHours(1);
        setField(orderCancelService, "lastScanTime", lastScanTime);

        final CartItem cartItem = createCartItem();
        final List<CartItem> cartItems = List.of(cartItem);
        final Order order = Order.from(cartItem.getMemberId(), cartItems);
        order.startPay(PayType.TOSS);

        doReturn(List.of(order)).when(orderRepository)
                .findAllByStatusInAndLastModifiedAtBetweenWithOrderItems(any(), any(), any());
        doThrow(new RuntimeException()).when(orderService).cancelPayingOrder(order);

        // when
        orderCancelService.cancelIncompleteOrders();

        // then
        assertThat(getField(orderCancelService, "lastScanTime")).isEqualTo(lastScanTime);
    }
}
