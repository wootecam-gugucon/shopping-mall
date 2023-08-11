package shopping.cart.domain.entity;

import shopping.cart.domain.vo.Quantity;
import shopping.cart.domain.vo.WonMoney;

import javax.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    private String productName;
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private WonMoney price;
    private String imageFileName;
    @Embedded
    private Quantity quantity;

    protected OrderItem() {
    }

    public OrderItem(final Long id, final Order order, final String productName, final WonMoney price,
                     final String imageFileName, final Quantity quantity) {
        this.id = id;
        this.order = order;
        this.productName = productName;
        this.price = price;
        this.imageFileName = imageFileName;
        this.quantity = quantity;
    }

    public static OrderItem from(final CartItem cartItem, final Order order) {
        return new OrderItem(null, order, cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getProduct().getImageFileName(), cartItem.getQuantity());
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public String getProductName() {
        return productName;
    }

    public WonMoney getPrice() {
        return price;
    }

    public WonMoney getTotalPrice() {
        return price.multiply(quantity);
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public Quantity getQuantity() {
        return quantity;
    }
}
