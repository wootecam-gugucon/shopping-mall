package shopping.cart.domain.entity;

import shopping.auth.domain.entity.User;
import shopping.cart.domain.vo.DollarMoney;
import shopping.cart.domain.vo.ExchangeRate;
import shopping.cart.domain.vo.WonMoney;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    private static final long MAX_TOTAL_PRICE = 100_000_000_000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    private WonMoney totalPrice;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "exchange_rate"))
    private ExchangeRate exchangeRate;

    protected Order() {
    }

    public Order(final Long id, final User user, final ExchangeRate exchangeRate) {
        this.id = id;
        this.user = user;
        this.totalPrice = WonMoney.ZERO;
        this.exchangeRate = exchangeRate;
    }

    public static Order from(final User user, final List<CartItem> cartItems, final ExchangeRate exchangeRate) {
        Order order = new Order(null, user, exchangeRate);
        cartItems.stream()
                .map(cartItem -> OrderItem.from(cartItem, order))
                .forEach(order::addOrderItem);
        return order;
    }

    private void addOrderItem(final OrderItem orderItem) {
        orderItems.add(orderItem);
        totalPrice = totalPrice.add(orderItem.getTotalPrice());
    }

    public DollarMoney getTotalPriceInDollar() {
        return exchangeRate.convert(totalPrice);
    }

    public static void validateTotalPrice(final List<CartItem> cartItems) {
        final BigInteger totalPrice = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigInteger.ZERO, BigInteger::add);
        validateRangeOf(totalPrice);
    }

    private static void validateRangeOf(final BigInteger totalPrice) {
        if (totalPrice.compareTo(BigInteger.valueOf(MAX_TOTAL_PRICE)) > 0) {
            throw new ShoppingException(ErrorCode.EXCEED_MAX_TOTAL_PRICE);
        }
    }

    public void validateUserHasId(Long userId) {
        if (!Objects.equals(user.getId(), userId)) {
            throw new ShoppingException(ErrorCode.INVALID_ORDER);
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

    public WonMoney getTotalPrice() {
        return totalPrice;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }
}
