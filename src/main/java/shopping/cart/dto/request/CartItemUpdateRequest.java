package shopping.cart.dto.request;

public final class CartItemUpdateRequest {

    private Integer quantity;

    private CartItemUpdateRequest() {
    }

    public CartItemUpdateRequest(final Integer quantity) {
        this.quantity = quantity;
    }


    public Integer getQuantity() {
        return quantity;
    }
}
