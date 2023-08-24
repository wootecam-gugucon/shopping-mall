package com.gugucon.shopping.item.controller.api;

import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.service.CartService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart/items")
public class CartItemController {

    private final CartService cartService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void insertCartItem(@RequestBody @Valid final CartItemInsertRequest cartItemInsertRequest,
                               @AuthenticationPrincipal final Long memberId) {
        cartService.insertCartItem(cartItemInsertRequest, memberId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CartItemResponse> getCartItems(@AuthenticationPrincipal final Long memberId) {
        return cartService.readCartItems(memberId);
    }

    @PatchMapping("/{cartItemId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCartItemQuantity(@PathVariable final Long cartItemId,
                                       @RequestBody @Valid final CartItemUpdateRequest cartItemUpdateRequest,
                                       @AuthenticationPrincipal final Long memberId) {
        cartService.updateCartItemQuantity(cartItemId, cartItemUpdateRequest, memberId);
    }

    @DeleteMapping("/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCartItem(@PathVariable final Long cartItemId, @AuthenticationPrincipal final Long memberId) {
        cartService.removeCartItem(cartItemId, memberId);
    }
}
