package shopping.cart.utils.currency;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import shopping.cart.domain.MoneyType;
import shopping.cart.domain.vo.ExchangeRate;
import shopping.cart.dto.response.ExchangeRateResponse;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

@Component
public class DefaultExchangeRateProvider implements ExchangeRateProvider {

    private static final String REQUEST_URL = "http://api.currencylayer.com/live";

    @Value("${currency-layer.secret-key}")
    private String accessKey;

    @Override
    public ExchangeRate fetchExchangeRateOf(final MoneyType moneyType) {
        final String requestUri =
            REQUEST_URL + "?currencies=" + MoneyType.KRW.name() + "&access_key=" + accessKey;
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity(requestUri,
            ExchangeRateResponse.class);
        validateFetch(response);
        final Map<String, Double> quotes = response.getBody().getQuotes();
        final Double rawExchangeRate = quotes.get(moneyType.name() + MoneyType.KRW.name());
        validateNotNull(rawExchangeRate);
        return new ExchangeRate(moneyType, rawExchangeRate);
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
