package shopping.cart.utils.currency;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import shopping.cart.domain.vo.ExchangeRate;
import shopping.cart.dto.response.ExchangeRateResponse;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

public class DefaultExchangeRateProvider implements ExchangeRateProvider {

    private static final String REQUEST_URL = "http://api.currencylayer.com/live";

    @Value("${currency-layer.secret-key}")
    private String accessKey;
    private final RestTemplate restTemplate;

    public DefaultExchangeRateProvider(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ExchangeRate fetchExchangeRate() {
        final String requestUri = REQUEST_URL + "?currencies=KRW&access_key=" + accessKey;
        final ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity(requestUri,
            ExchangeRateResponse.class);
        validateFetch(response);
        final Map<String, Double> quotes = response.getBody().getQuotes();
        final Double exchangeRateValue = quotes.get("USDKRW");
        validateNotNull(exchangeRateValue);
        return new ExchangeRate(exchangeRateValue);
    }

    private void validateFetch(final ResponseEntity<ExchangeRateResponse> response) {
        if (!response.hasBody() || !response.getBody().isSuccess()) {
            throw new ShoppingException(ErrorCode.FAILED_TO_FETCH_EXCHANGE_RATE);
        }
    }

    private void validateNotNull(final Double rawExchangeRate) {
        if (rawExchangeRate == null) {
            throw new ShoppingException(ErrorCode.FAILED_TO_FETCH_EXCHANGE_RATE);
        }
    }
}
