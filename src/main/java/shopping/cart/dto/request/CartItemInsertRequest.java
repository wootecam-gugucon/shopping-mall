package shopping.cart.dto.request;

public final class CartItemInsertRequest {

    private Long productId;

    private CartItemInsertRequest() {
    }

    public CartItemInsertRequest(final Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
