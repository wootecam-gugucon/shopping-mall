package shopping.cart.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static shopping.TestUtils.createProduct;
import static shopping.TestUtils.createUser;

import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CartItem 단위 테스트")
class CartItemTest {

    @Test
    @DisplayName("상품 금액과 주문 수량을 곱한 총 금액을 구한다.")
    void getTotalPrice() {
        /* given */
        final CartItem cartItem = new CartItem(1L, createUser(), createProduct("치킨", 10000), 4);

        /* when */
        final BigInteger totalPrice = cartItem.getTotalPrice();

        /* then */
        assertThat(totalPrice).isEqualTo(BigInteger.valueOf(40000));
    }

}
