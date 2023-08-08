package shopping.cart.utils.currency;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import shopping.cart.domain.MoneyType;

//@SpringBootTest(classes = DefaultExchangeRateProvider.class)
@DisplayName("환율 정보를 제공받는 외부 API 테스트")
class DefaultExchangeRateProviderTest {

    @Autowired
    DefaultExchangeRateProvider exchangeRateProvider;

    //@Test
    @DisplayName("환율 정보를 정상적으로 가져온다.")
    void fetchExchangeRateInfo() {
        /* given */

        /* when & then */
        assertThatNoException()
            .isThrownBy(() -> exchangeRateProvider.fetchExchangeRateOf(MoneyType.USD));
    }
}
