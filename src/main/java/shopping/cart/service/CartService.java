package shopping.cart.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.domain.entity.User;
import shopping.auth.repository.UserRepository;
import shopping.cart.domain.entity.CartItem;
import shopping.cart.domain.entity.Product;
import shopping.cart.domain.vo.Quantity;
import shopping.cart.dto.request.CartItemInsertRequest;
import shopping.cart.dto.request.CartItemUpdateRequest;
import shopping.cart.dto.response.CartItemResponse;
import shopping.cart.repository.CartItemRepository;
import shopping.cart.repository.ProductRepository;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

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
            .collect(Collectors.toUnmodifiableList());
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
