package shopping.cart.dto.response;

import shopping.cart.domain.entity.CartItem;

public final class CartItemResponse {

    private Long cartItemId;
    private String name;
    private String imageFileName;
    private long price;
    private int quantity;

    private CartItemResponse() {
    }

    private CartItemResponse(final Long cartItemId, final String name,
        final String imageFileName, final long price,
        final int quantity) {
        this.cartItemId = cartItemId;
        this.name = name;
        this.imageFileName = imageFileName;
        this.price = price;
        this.quantity = quantity;
    }

    public static CartItemResponse from(CartItem cartItem) {
        return new CartItemResponse(
            cartItem.getId(),
            cartItem.getProduct().getName(),
            cartItem.getProduct().getImageFileName(),
            cartItem.getProduct().getPrice().getValue(),
            cartItem.getQuantity().getValue()
        );
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public String getName() {
        return name;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public long getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
