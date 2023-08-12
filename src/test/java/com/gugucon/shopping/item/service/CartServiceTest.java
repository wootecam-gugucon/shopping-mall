package com.gugucon.shopping.item.service;

import com.gugucon.shopping.user.repository.UserRepository;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.common.domain.vo.WonMoney;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.repository.CartItemRepository;
import com.gugucon.shopping.item.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService 단위 테스트")
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니에 상품을 추가한다.")
    void insertCartItem() {
        /* given */
        final Long userId = 1L;
        final Product product = new Product(1L, "치킨", "fried_chicken.png", new WonMoney(20000));
        final CartItemInsertRequest cartRequest = new CartItemInsertRequest(product.getId());
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.existsByUserIdAndProductId(userId, product.getId())).thenReturn(false);

        /* when */
        cartService.insertCartItem(cartRequest, userId);

        /* then */
        verify(cartItemRepository).save(any());
    }

    @Test
    @DisplayName("장바구니 상품을 조회한다.")
    void readCartItems() {
        /* given */
        final Long userId = 1L;
        final Product chicken = new Product(1L, "치킨", "fried_chicken.png", new WonMoney(20000));
        final Product pizza = new Product(2L, "피자", "pizza.png", new WonMoney(25000));
        final CartItem cartItemChicken = new CartItem(1L, userId, chicken, 1);
        final CartItem cartItemPizza = new CartItem(2L, userId, pizza, 1);
        final List<CartItem> cartItems = List.of(cartItemChicken, cartItemPizza);
        when(cartItemRepository.findByUserId(userId)).thenReturn(cartItems);

        /* when */
        final List<CartItemResponse> cartItemResponses = cartService.getCartItems(userId);

        /* then */
        final List<String> productNames = cartItemResponses.stream()
                .map(CartItemResponse::getName)
                .collect(Collectors.toList());

        assertThat(productNames).containsExactly("치킨", "피자");
        assertThat(cartItemResponses).hasSize(2);
    }

    @Test
    @DisplayName("장바구니 상품 수량을 수정한다.")
    void updateCartItem() {
        /* given */
        final Long userId = 1L;
        final Product product = new Product(1L, "치킨", "fried_chicken.png", new WonMoney(20000));
        final CartItem cartItem = new CartItem(1L, userId, product, 1);

        final int updateQuantity = 3;
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));

        /* when */
        cartService.updateCartItemQuantity(cartItem.getId(),
                new CartItemUpdateRequest(updateQuantity), userId);

        /* then */
        assertThat(cartItem.getQuantity()).isEqualTo(new Quantity(updateQuantity));
    }

    @Test
    @DisplayName("장바구니 상품을 삭제한다.")
    void deleteCartItem() {
        /* given */
        final Long userId = 1L;
        final Product product = new Product(1L, "치킨", "fried_chicken.png", new WonMoney(20000));
        final CartItem cartItem = new CartItem(1L, userId, product, 1);

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));

        /* when */
        cartService.removeCartItem(cartItem.getId(), userId);

        /* then */
        verify(cartItemRepository).delete(cartItem);
    }
}
