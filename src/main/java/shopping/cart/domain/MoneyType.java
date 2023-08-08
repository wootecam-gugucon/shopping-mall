package shopping.cart.domain;

public enum MoneyType {

    USD("달러"),
    KRW("원"),
    ;

    private final String unit;

    MoneyType(final String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }
}
