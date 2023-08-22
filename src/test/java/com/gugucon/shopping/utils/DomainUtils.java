package com.gugucon.shopping.utils;

import com.gugucon.shopping.common.domain.vo.Money;
import com.gugucon.shopping.common.domain.vo.Quantity;
import com.gugucon.shopping.item.domain.entity.CartItem;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.domain.vo.Nickname;
import com.gugucon.shopping.member.domain.vo.Password;
import com.gugucon.shopping.order.domain.entity.OrderItem;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DomainUtils {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static Long sequence = 0L;

    public static Product createProduct(final String name, final long price) {
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

    public static Product createProduct(final int stock) {
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

    public static Product createProductWithoutId(final String name, final long price, final int stock) {
        return Product.builder()
                .name(name)
                .imageFileName("image_file_" + name)
                .stock(Quantity.from(stock))
                .description("test_description")
                .price(Money.from(price))
                .build();
    }

    public static Product createSoldOutProduct(final String name, final long price) {
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

    public static OrderItem createOrderItem(final String name, final Long productId, final Quantity quantity) {
        sequence++;
        return OrderItem.builder()
                .id(sequence)
                .name(name)
                .productId(productId)
                .imageFileName("")
                .quantity(quantity)
                .price(Money.ZERO).build();
    }
}
