package shopping.cart.utils.currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import shopping.cart.domain.vo.ExchangeRate;

@DisplayName("DefaultExchangeRateProvider 단위 테스트")
class DefaultExchangeRateProviderTest {

    DefaultExchangeRateProvider exchangeRateProvider;
    @Value("${currency-layer.secret-key}")
    String accessKey;

    @BeforeEach
    void setUp() {
        final RestTemplate restTemplate = new RestTemplate();
        final String requestURI =
            "http://api.currencylayer.com/live?currencies=KRW&access_key=" + accessKey;
        final double mockExchangeRate = 1234.567890;

        final MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(ExpectedCount.once(), requestTo(requestURI))
            .andExpect(method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(
                "{\"success\":true,\"terms\":\"https:\\/\\/currencylayer.com\\/terms\",\"privacy\":\"https:\\/\\/currencylayer.com\\/privacy\",\"timestamp\":1691561942,\"source\":\"USD\",\"quotes\":{\"USDKRW\":"
                    + mockExchangeRate + "}}",
                MediaType.APPLICATION_JSON));

        exchangeRateProvider = new DefaultExchangeRateProvider(restTemplate);
    }

    @Test
    @DisplayName("환율 정보를 정상적으로 가져온다.")
    void fetchExchangeRateInfo() {
        /* given */

        /* when */
        final ExchangeRate exchangeRate = exchangeRateProvider.fetchExchangeRate();

        /* then */
        assertThat(exchangeRate.getValue()).isEqualTo(1234.567890);
    }
}
