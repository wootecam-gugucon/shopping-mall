package shopping.cart.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.auth.domain.entity.User;
import shopping.cart.domain.vo.ExchangeRate;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static shopping.TestUtils.createProduct;
import static shopping.TestUtils.createUser;

@DisplayName("Order 단위 테스트")
class OrderTest {

    @Test
    @DisplayName("총 주문 금액을 달러로 환산해서 반환한다.")
    void getTotalPriceInDollar() {
        /* given */
        final User user = createUser();
        final CartItem cartItem1 = new CartItem(1L, user, createProduct("치킨", 10000), 5);
        final CartItem cartItem2 = new CartItem(2L, user, createProduct("피자", 20000), 4);
        final Order order = Order.from(user, List.of(cartItem1, cartItem2), new ExchangeRate(1300));

        /* when & then */
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

    @Test
    @DisplayName("주문자의 ID를 검증한다.")
    void validateUserHasId() {
        /* given */
        User user = createUser();
        Order order = Order.from(user, Collections.emptyList(), new ExchangeRate(1300));

        /* when & then */
        assertThatNoException().isThrownBy(() -> order.validateUserHasId(user.getId()));
        ShoppingException exception = assertThrows(ShoppingException.class, () -> order.validateUserHasId(Long.MAX_VALUE));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER);
    }
}
