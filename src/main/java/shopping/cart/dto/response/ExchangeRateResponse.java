package shopping.cart.dto.response;

import java.util.Map;

public final class ExchangeRateResponse {

    private boolean success;
    private String source;
    private Map<String, Double> quotes;

    private ExchangeRateResponse() {
    }

    public ExchangeRateResponse(final boolean success, final String source,
        final Map<String, Double> quotes) {
        this.success = success;
        this.source = source;
        this.quotes = quotes;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getSource() {
        return source;
    }

    public Map<String, Double> getQuotes() {
        return quotes;
    }
}
