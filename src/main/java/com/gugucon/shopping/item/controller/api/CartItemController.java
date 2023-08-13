package com.gugucon.shopping.item.controller.api;

import com.gugucon.shopping.item.controller.validator.CartItemInsertRequestValidator;
import com.gugucon.shopping.item.controller.validator.CartItemUpdateRequestValidator;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.service.CartService;
import com.gugucon.shopping.member.argumentresolver.annotation.MemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart/items")
public class CartItemController {

    private final CartService cartService;
    private final CartItemInsertRequestValidator insertRequestValidator;
    private final CartItemUpdateRequestValidator updateRequestValidator;

    @InitBinder("cartItemInsertRequest")
    public void initCartItemInsertRequest(WebDataBinder dataBinder) {
        dataBinder.addValidators(insertRequestValidator);
    }

    @InitBinder("cartItemUpdateRequest")
    public void initCartItemUpdateRequest(WebDataBinder dataBinder) {
        dataBinder.addValidators(updateRequestValidator);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void insertCartItem(@RequestBody @Validated CartItemInsertRequest cartItemInsertRequest,
                               @MemberId Long memberId) {
        cartService.insertCartItem(cartItemInsertRequest, memberId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CartItemResponse> getCartItems(@MemberId Long memberId) {
        return cartService.readCartItems(memberId);
    }

    @PutMapping("/{cartItemId}/quantity")
    @ResponseStatus(HttpStatus.OK)
    public void updateCartItemQuantity(@PathVariable Long cartItemId,
                                       @RequestBody @Validated CartItemUpdateRequest cartItemUpdateRequest,
                                       @MemberId Long memberId) {
        cartService.updateCartItemQuantity(cartItemId, cartItemUpdateRequest, memberId);
    }

    @DeleteMapping("/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCartItem(@PathVariable Long cartItemId, @MemberId Long memberId) {
        cartService.removeCartItem(cartItemId, memberId);
    }
}
