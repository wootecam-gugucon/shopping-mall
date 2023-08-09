package shopping.cart.domain.vo;

import java.math.BigDecimal;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ExchangeRate 단위 테스트")
class ExchangeRateTest {

    @Test
    @DisplayName("원 단위 금액을 달러 단위 금액으로 변환한다.")
    void convertToDollar() {
        /* given */
        final ExchangeRate exchangeRate = new ExchangeRate(1300);
        final Money money = new Money(1300);

        /* when */
        final DollarMoney dollarMoney = exchangeRate.convert(money);

        /* then */
        Assertions.assertThat(dollarMoney).isEqualTo(new DollarMoney(BigDecimal.ONE));
    }

}
