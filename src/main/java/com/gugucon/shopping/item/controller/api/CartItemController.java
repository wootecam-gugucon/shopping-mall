package com.gugucon.shopping.item.controller.api;

import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.service.CartService;
import com.gugucon.shopping.member.argumentresolver.annotation.MemberId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart/items")
public class CartItemController {

    private final CartService cartService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void insertCartItem(@RequestBody @Valid final CartItemInsertRequest cartItemInsertRequest,
                               @MemberId final Long memberId) {
        cartService.insertCartItem(cartItemInsertRequest, memberId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CartItemResponse> getCartItems(@MemberId final Long memberId) {
        return cartService.readCartItems(memberId);
    }

    @PutMapping("/{cartItemId}/quantity")
    @ResponseStatus(HttpStatus.OK)
    public void updateCartItemQuantity(@PathVariable final Long cartItemId,
                                       @RequestBody @Valid final CartItemUpdateRequest cartItemUpdateRequest,
                                       @MemberId final Long memberId) {
        cartService.updateCartItemQuantity(cartItemId, cartItemUpdateRequest, memberId);
    }

    @DeleteMapping("/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCartItem(@PathVariable final Long cartItemId, @MemberId final Long memberId) {
        cartService.removeCartItem(cartItemId, memberId);
    }
}
