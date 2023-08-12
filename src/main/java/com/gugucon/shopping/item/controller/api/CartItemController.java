package com.gugucon.shopping.item.controller.api;

import java.util.List;

import com.gugucon.shopping.item.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.gugucon.shopping.member.argumentresolver.annotation.MemberId;
import com.gugucon.shopping.item.controller.validator.CartItemInsertRequestValidator;
import com.gugucon.shopping.item.controller.validator.CartItemUpdateRequestValidator;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;

@RestController
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

    public CartItemController(final CartService cartService,
        final CartItemInsertRequestValidator insertRequestValidator,
        final CartItemUpdateRequestValidator updateRequestValidator) {
        this.cartService = cartService;
        this.insertRequestValidator = insertRequestValidator;
        this.updateRequestValidator = updateRequestValidator;
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
        return cartService.getCartItems(memberId);
    }

    @PutMapping("/{cartItemId}/quantity")
    @ResponseStatus(HttpStatus.OK)
    public void updateCartItemQuantity(@PathVariable Long cartItemId,
        @RequestBody @Validated CartItemUpdateRequest cartItemUpdateRequest, @MemberId Long memberId) {
        cartService.updateCartItemQuantity(cartItemId, cartItemUpdateRequest, memberId);
    }

    @DeleteMapping("/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCartItem(@PathVariable Long cartItemId, @MemberId Long memberId) {
        cartService.removeCartItem(cartItemId, memberId);
    }
}
