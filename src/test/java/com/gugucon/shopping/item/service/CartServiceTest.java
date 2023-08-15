package com.gugucon.shopping.item.service;

import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.domain.entity.Product;
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

import static com.gugucon.shopping.TestUtils.createProduct;
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
    private ProductRepository productRepository;
    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니에 상품을 추가한다.")
    void insertCartItem() {
        /* given */
        final Long memberId = 1L;
        final Product product = createProduct("치킨", 10000);
        final CartItemInsertRequest cartRequest = new CartItemInsertRequest(product.getId());
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.existsByMemberIdAndProductId(memberId, product.getId())).thenReturn(false);

        /* when */
        cartService.insertCartItem(cartRequest, memberId);

        /* then */
        verify(cartItemRepository).save(any());
    }

    @Test
    @DisplayName("장바구니 상품을 조회한다.")
    void readCartItems() {
        /* given */
        final Long memberId = 1L;
        final Product chicken = createProduct("치킨", 10000);
        final Product pizza = createProduct("피자", 20000);
        final CartItem cartItemChicken = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(chicken)
                .quantity(1)
                .build();
        final CartItem cartItemPizza = CartItem.builder()
                .id(2L)
                .memberId(memberId)
                .product(pizza)
                .quantity(1)
                .build();
        final List<CartItem> cartItems = List.of(cartItemChicken, cartItemPizza);
        when(cartItemRepository.findByMemberId(memberId)).thenReturn(cartItems);

        /* when */
        final List<CartItemResponse> cartItemResponses = cartService.readCartItems(memberId);

        /* then */
        final List<String> productNames = cartItemResponses.stream()
                .map(CartItemResponse::getName)
                .collect(Collectors.toList());

        assertThat(productNames).containsExactly("치킨", "피자");
        assertThat(cartItemResponses).hasSize(2);
    }

    @Test
    @DisplayName("장바구니 상품 수량을 수정한다.")
    void updateCartItemQuantity() {
        /* given */
        final Long memberId = 1L;
        final Product product = createProduct("치킨", 10000);
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(product)
                .quantity(1)
                .build();

        final int updateQuantity = 3;
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));

        /* when */
        cartService.updateCartItemQuantity(cartItem.getId(),
                new CartItemUpdateRequest(updateQuantity), memberId);

        /* then */
        assertThat(cartItem.getQuantity()).isEqualTo(Quantity.from(updateQuantity));
    }

    @Test
    @DisplayName("장바구니 상품을 삭제한다.")
    void removeCartItem() {
        /* given */
        final Long memberId = 1L;
        final Product product = createProduct("치킨", 10000);
        final CartItem cartItem = CartItem.builder()
                .id(1L)
                .memberId(memberId)
                .product(product)
                .quantity(1)
                .build();

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));

        /* when */
        cartService.removeCartItem(cartItem.getId(), memberId);

        /* then */
        verify(cartItemRepository).delete(cartItem);
    }
}
