package shopping.cart.domain.entity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import shopping.auth.domain.entity.User;
import shopping.cart.domain.vo.Money;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

@Entity
@Table(name = "orders")
public class Order {

    private static final long MAX_TOTAL_PRICE = 100_000_000_000L;

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    private Money totalPrice;

    protected Order() {
    }

    public Order(final Long id, final User user) {
        this.id = id;
        this.user = user;
        this.totalPrice = Money.ZERO;
    }

    public static Order of(final User user) {
        return new Order(null, user);
    }

    public void addOrderItem(final OrderItem orderItem) {
        orderItems.add(orderItem);
        totalPrice = totalPrice.add(orderItem.getTotalPrice());
    }

    public static void validateTotalPrice(final List<CartItem> cartItems) {
        final BigInteger totalPrice = cartItems.stream()
            .map(CartItem::getTotalPrice)
            .reduce(BigInteger.ZERO, BigInteger::add);
        validateRangeOf(totalPrice);
    }

    public boolean hasUser(final User user) {
        return this.user == user;
    }

    private static void validateRangeOf(final BigInteger totalPrice) {
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

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }
}
