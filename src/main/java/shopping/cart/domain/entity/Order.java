package shopping.cart.domain.entity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import shopping.auth.domain.entity.User;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

@Entity
@Table(name = "orders")
public class Order {

    private static final long MAX_TOTAL_PRICE = 100_000_000_000L;

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    protected Order() {
    }

    public Order(final Long id, final User user) {
        this.id = id;
        this.user = user;
    }

    public static Order of(final User user) {
        return new Order(null, user);
    }

    public void addOrderItem(final OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public static void validateTotalPrice(final List<CartItem> cartItems) {
        final BigInteger totalPrice = cartItems.stream()
            .map(CartItem::getTotalPrice)
            .reduce(BigInteger.ZERO, BigInteger::add);
        validateTotalPriceRange(totalPrice);
    }

    private static void validateTotalPriceRange(final BigInteger totalPrice) {
        if (totalPrice.compareTo(BigInteger.valueOf(MAX_TOTAL_PRICE)) > 0) {
            throw new ShoppingException(ErrorCode.EXCEED_MAX_TOTAL_PRICE);
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }
}
