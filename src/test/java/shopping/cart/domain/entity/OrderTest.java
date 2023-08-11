package shopping.cart.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.cart.domain.vo.ExchangeRate;
import shopping.cart.domain.vo.Quantity;
import shopping.cart.domain.vo.WonMoney;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static shopping.TestUtils.createProduct;
import static shopping.TestUtils.createUser;

@DisplayName("Order 단위 테스트")
class OrderTest {

    @Test
    @DisplayName("주문에 주문상품을 추가한다.")
    void addOrderItem() {
        /* given */
        final Order order = Order.of(createUser(), new ExchangeRate(1300));
        final OrderItem 치킨 = new OrderItem(1L, order, "치킨", new WonMoney(10000), "chicken.png",
                new Quantity(3));
        final OrderItem 피자 = new OrderItem(2L, order, "피자", new WonMoney(20000), "pizza.png",
                new Quantity(4));

        /* when */
        order.addOrderItem(치킨);
        order.addOrderItem(피자);

        /* then */
        assertThat(order.getOrderItems()).hasSize(2);
        assertThat(order.getTotalPrice()).isEqualTo(new WonMoney(110000));
    }

    @Test
    @DisplayName("총 주문 금액을 달러로 환산해서 반환한다.")
    void getTotalPriceInDollar() {
        /* given */
        final Order order = Order.of(createUser(), new ExchangeRate(1300));
        final OrderItem 치킨 = new OrderItem(1L, order, "치킨", new WonMoney(10000), "chicken.png",
                new Quantity(5));
        final OrderItem 피자 = new OrderItem(2L, order, "피자", new WonMoney(20000), "pizza.png",
                new Quantity(4));

        /* when */
        order.addOrderItem(치킨);
        order.addOrderItem(피자);

        /* then */
        assertThat(order.getTotalPriceInDollar().getValue()).isEqualTo(100);
    }

    @Test
    @DisplayName("주문 총액은 100_000_000_000원을 넘을 수 없다.")
    void validateTotalPrice() {
        /* given */
        final CartItem validCartItem = new CartItem(1L, createUser(),
                createProduct("치킨", 100_000_000_000L), 1);
        final CartItem invalidCartItem = new CartItem(2L, createUser(),
                createProduct("피자", 100_000_000_001L), 1);

        /* when & then */
        assertThatNoException()
                .isThrownBy(() -> Order.validateTotalPrice(List.of(validCartItem)));
        final ShoppingException exception = assertThrows(ShoppingException.class,
                () -> Order.validateTotalPrice(List.of(invalidCartItem)));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXCEED_MAX_TOTAL_PRICE);
    }
}
