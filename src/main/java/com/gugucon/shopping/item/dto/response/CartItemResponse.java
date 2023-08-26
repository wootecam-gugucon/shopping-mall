package com.gugucon.shopping.item.dto.response;

import com.gugucon.shopping.item.domain.entity.CartItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class CartItemResponse {

    private Long cartItemId;
    private Long productId;
    private String name;
    private String imageFileName;
    private long price;
    private int quantity;
    private boolean soldOut;

    public static CartItemResponse from(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getImageFileName(),
                cartItem.getProduct().getPrice().getValue(),
                cartItem.getQuantity().getValue(),
                cartItem.getProduct().getStock().isZero()
        );
    }
}
