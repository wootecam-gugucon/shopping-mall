package com.gugucon.shopping.cart.service;

import com.gugucon.shopping.auth.domain.entity.User;
import com.gugucon.shopping.auth.repository.UserRepository;
import com.gugucon.shopping.cart.domain.entity.CartItem;
import com.gugucon.shopping.cart.domain.entity.Product;
import com.gugucon.shopping.cart.domain.vo.Quantity;
import com.gugucon.shopping.cart.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.cart.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.cart.dto.response.CartItemResponse;
import com.gugucon.shopping.cart.repository.CartItemRepository;
import com.gugucon.shopping.cart.repository.ProductRepository;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(final CartItemRepository cartItemRepository,
                       final UserRepository userRepository,
                       final ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void insertCartItem(CartItemInsertRequest cartItemInsertRequest, Long userId) {
        final Long productId = cartItemInsertRequest.getProductId();
        final Product product = findProductBy(productId);
        validateProductNotInCart(userId, productId);

        final User user = userRepository.getReferenceById(userId);
        final CartItem cartItem = new CartItem(user, product);
        cartItemRepository.save(cartItem);
    }

    public List<CartItemResponse> getCartItems(final Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(CartItemResponse::from)
                .toList();
    }

    @Transactional
    public void updateCartItemQuantity(final Long cartItemId,
                                       final CartItemUpdateRequest cartItemUpdateRequest, final Long userId) {
        final CartItem cartItem = findCartItemBy(cartItemId, userId);

        final Quantity updateQuantity = new Quantity(cartItemUpdateRequest.getQuantity());

        if (updateQuantity.isZero()) {
            cartItemRepository.delete(cartItem);
            return;
        }

        cartItem.updateQuantity(updateQuantity);
    }

    @Transactional
    public void removeCartItem(final Long cartItemId, final Long userId) {
        final CartItem cartItem = findCartItemBy(cartItemId, userId);
        cartItemRepository.delete(cartItem);
    }

    private CartItem findCartItemBy(final Long cartItemId, final Long userId) {
        final CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_CART_ITEM));
        final User user = userRepository.getReferenceById(userId);

        validateUserHasCartItem(user, cartItem);
        return cartItem;
    }

    private void validateUserHasCartItem(final User user, final CartItem cartItem) {
        if (!cartItem.hasUser(user)) {
            throw new ShoppingException(ErrorCode.INVALID_CART_ITEM);
        }
    }

    private Product findProductBy(final Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_PRODUCT));
    }

    private void validateProductNotInCart(final Long userId, final Long productId) {
        if (cartItemRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ShoppingException(ErrorCode.DUPLICATE_CART_ITEM);
        }
    }
}
