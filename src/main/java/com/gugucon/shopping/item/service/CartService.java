package com.gugucon.shopping.item.service;

import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.domain.vo.Stock;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.item.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void insertCartItem(final CartItemInsertRequest cartItemInsertRequest, final Long memberId) {
        final Long productId = cartItemInsertRequest.getProductId();
        final Product product = findProductBy(productId);
        validateProductNotInCart(memberId, productId);
        product.validateStock();

        final CartItem cartItem = CartItem.builder()
                .memberId(memberId)
                .product(product)
                .quantity(1)
                .build();
        cartItemRepository.save(cartItem);
    }

    public List<CartItemResponse> readCartItems(final Long memberId) {
        return cartItemRepository.findByMemberId(memberId).stream()
                .map(CartItemResponse::from)
                .toList();
    }

    @Transactional
    public void updateCartItemQuantity(final Long cartItemId,
                                       final CartItemUpdateRequest cartItemUpdateRequest,
                                       final Long memberId) {
        final CartItem cartItem = findCartItemBy(cartItemId, memberId);

        final Quantity updateQuantity = Quantity.from(cartItemUpdateRequest.getQuantity());

        if (updateQuantity.isZero()) {
            cartItemRepository.delete(cartItem);
            return;
        }

        cartItem.updateQuantity(updateQuantity);
    }

    @Transactional
    public void removeCartItem(final Long cartItemId, final Long memberId) {
        final CartItem cartItem = findCartItemBy(cartItemId, memberId);
        cartItemRepository.delete(cartItem);
    }

    private CartItem findCartItemBy(final Long cartItemId, final Long memberId) {
        final CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_CART_ITEM));

        cartItem.validateUserHasId(memberId);
        return cartItem;
    }

    private Product findProductBy(final Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_PRODUCT));
    }

    private void validateProductNotInCart(final Long memberId, final Long productId) {
        if (cartItemRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new ShoppingException(ErrorCode.DUPLICATE_CART_ITEM);
        }
    }
}
