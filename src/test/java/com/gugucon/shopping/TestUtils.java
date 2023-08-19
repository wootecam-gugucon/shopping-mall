package com.gugucon.shopping;

import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.Password;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestUtils {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static Long sequence = 0L;

    public static Product createProduct(String name, long price) {
        sequence++;
        return Product.builder()
                .id(sequence)
                .name(name)
                .imageFileName("image_file_name_" + sequence)
                .stock(100)
                .description("test_description")
                .price(price)
                .build();
    }

    public static Product createProduct(int stock) {
        sequence++;
        return Product.builder()
                .id(sequence)
                .name("name")
                .imageFileName("image_file_name_" + sequence)
                .stock(stock)
                .description("test_description")
                .price(1000L)
                .build();
    }

    public static Product createSoldOutProduct(String name, long price) {
        sequence++;
        return Product.builder()
                .id(sequence)
                .name(name)
                .imageFileName("image_file_name_" + sequence)
                .stock(0)
                .description("test_description")
                .price(price)
                .build();
    }

    public static Member createMember() {
        sequence++;
        return Member.builder()
                .id(sequence)
                .email("test_email" + sequence + "@gmail.com")
                .password(Password.of("test_password", passwordEncoder))
                .nickname("test_nickname_" + sequence)
                .build();
    }

    public static CartItem createCartItem() {
        sequence++;
        return CartItem.builder()
                .id(sequence)
                .memberId(1L)
                .product(createProduct(100))
                .quantity(1)
                .build();
    }


}
