package shopping.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        User user = new User(1L, "test_email@woowafriends.com", "test_password!");
        Product product = new Product(1L, "치킨", "fried_chicken.png", 20000);
        CartItemInsertRequest cartRequest = new CartItemInsertRequest(product.getId());
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        /* when */
        cartService.insertCartItem(cartRequest, user.getId());

        /* then */
        verify(cartItemRepository).save(any());
    }

    @Test
    @DisplayName("장바구니 상품을 조회한다.")
    void readCartItems() {
        /* given */
        User user = new User(1L, "test_email@woowafriends.com", "test_password!");
        Product chicken = new Product(1L, "치킨", "fried_chicken.png", 20000);
        Product pizza = new Product(2L, "피자", "pizza.png", 25000);
        CartItem cartItemChicken = new CartItem(1L, user, chicken, 1);
        CartItem cartItemPizza = new CartItem(2L, user, pizza, 1);
        List<CartItem> cartItems = List.of(cartItemChicken, cartItemPizza);
        when(cartItemRepository.findByUserId(user.getId())).thenReturn(cartItems);

        /* when */
        List<CartItemResponse> cartItemResponses = cartService.getCartItems(user.getId());

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
        User user = new User(1L, "test_email@woowafriends.com", "test_password!");
        Product product = new Product(1L, "치킨", "fried_chicken.png", 20000);
        CartItem cartItem = new CartItem(1L, user, product, 1);

        int updateQuantity = 3;
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        /* when */
        cartService.updateCartItemQuantity(cartItem.getId(),
            new CartItemUpdateRequest(updateQuantity), user.getId());

        /* then */
        assertThat(cartItem.getQuantity()).isEqualTo(new Quantity(updateQuantity));
    }

    @Test
    @DisplayName("장바구니 상품을 삭제한다.")
    void deleteCartItem() {
        /* given */
        User user = new User(1L, "test_email@woowafriends.com", "test_password!");
        Product product = new Product(1L, "치킨", "fried_chicken.png", 20000);
        CartItem cartItem = new CartItem(1L, user, product, 1);

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        /* when */
        cartService.removeCartItem(cartItem.getId(), user.getId());

        /* then */
        verify(cartItemRepository).delete(cartItem);
    }
}
