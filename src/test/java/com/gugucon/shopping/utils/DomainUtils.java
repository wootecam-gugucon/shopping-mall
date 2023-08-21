package com.gugucon.shopping.utils;

import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.domain.vo.Nickname;
import com.gugucon.shopping.member.domain.vo.Password;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DomainUtils {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static Long sequence = 0L;

    public static Product createProduct(String name, long price) {
        sequence++;
        return Product.builder()
                .id(sequence)
                .name(name)
                .imageFileName("image_file_name_" + sequence)
                .stock(Quantity.from(100))
                .description("test_description")
                .price(Money.from(price))
                .build();
    }

    public static Product createProduct(int stock) {
        sequence++;
        return Product.builder()
                .id(sequence)
                .name("name")
                .imageFileName("image_file_name_" + sequence)
                .stock(Quantity.from(stock))
                .description("test_description")
                .price(Money.from(1000L))
                .build();
    }

    public static Product createProductWithoutId(String name, long price) {
        sequence++;
        return Product.builder()
                .name(name)
                .imageFileName("image_file_name_" + sequence)
                .stock(Quantity.from(100))
                .description("test_description")
                .price(Money.from(price))
                .build();
    }

    public static Product createSoldOutProduct(String name, long price) {
        sequence++;
        return Product.builder()
                .id(sequence)
                .name(name)
                .imageFileName("image_file_name_" + sequence)
                .stock(Quantity.from(0))
                .description("test_description")
                .price(Money.from(price))
                .build();
    }

    public static Member createMember() {
        sequence++;
        return Member.builder()
                .id(sequence)
                .email(Email.from("test_email" + sequence + "@gmail.com"))
                .password(Password.of("test_password", passwordEncoder))
                .nickname(Nickname.from("test_nickname_" + sequence))
                .build();
    }

    public static CartItem createCartItem() {
        sequence++;
        return CartItem.builder()
                .id(sequence)
                .memberId(1L)
                .product(createProduct(100))
                .quantity(Quantity.from(1))
                .build();
    }
}
