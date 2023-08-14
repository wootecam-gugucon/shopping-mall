package com.gugucon.shopping.item.controller.api;

import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.service.CartService;
import com.gugucon.shopping.member.argumentresolver.annotation.MemberId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public void insertCartItem(@RequestBody @Validated final CartItemInsertRequest cartItemInsertRequest,
                               @AuthenticationPrincipal final Long memberId) {
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
                                       @RequestBody @Validated final CartItemUpdateRequest cartItemUpdateRequest,
                                       @MemberId final Long memberId) {
        cartService.updateCartItemQuantity(cartItemId, cartItemUpdateRequest, memberId);
    }

    @DeleteMapping("/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCartItem(@PathVariable final Long cartItemId, @MemberId final Long memberId) {
        cartService.removeCartItem(cartItemId, memberId);
    }
}
