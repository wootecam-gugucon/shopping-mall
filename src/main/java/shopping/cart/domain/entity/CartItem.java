package shopping.cart.domain.entity;

import shopping.auth.domain.entity.User;
import shopping.cart.domain.vo.Quantity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "cart_item")
public class CartItem {

    private static final int DEFAULT_QUANTITY = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Embedded
    private Quantity quantity;

    protected CartItem() {
    }

    public CartItem(final Long id, final User user, final Product product,
                    final int quantity) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.quantity = new Quantity(quantity);
    }

    public CartItem(final User user, final Product product) {
        this(null, user, product, DEFAULT_QUANTITY);
    }

    public void updateQuantity(final Quantity quantity) {
        this.quantity = quantity;
    }

    public boolean hasUser(final User user) {
        return this.user == user;
    }

    public BigInteger getTotalPrice() {
        return BigInteger.valueOf(product.getPrice().getValue())
                .multiply(BigInteger.valueOf(quantity.getValue()));
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
    }

    public Quantity getQuantity() {
        return quantity;
    }
}
