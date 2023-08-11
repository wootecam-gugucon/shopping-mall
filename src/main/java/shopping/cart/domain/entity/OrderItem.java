package shopping.cart.domain.entity;

import shopping.cart.domain.vo.Money;
import shopping.cart.domain.vo.Quantity;

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
    private Money price;
    private String imageFileName;
    @Embedded
    private Quantity quantity;

    protected OrderItem() {
    }

    public OrderItem(final Long id, final Order order, final String productName, final Money price,
                     final String imageFileName, final Quantity quantity) {
        this.id = id;
        this.order = order;
        this.productName = productName;
        this.price = price;
        this.imageFileName = imageFileName;
        this.quantity = quantity;
    }

    public static OrderItem from(final CartItem cartItem, final Order order) {
        final OrderItem orderItem = new OrderItem(null, order, cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getProduct().getImageFileName(), cartItem.getQuantity());
        order.addOrderItem(orderItem);
        return orderItem;
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

    public Money getPrice() {
        return price;
    }

    public Money getTotalPrice() {
        return price.multiply(quantity);
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public Quantity getQuantity() {
        return quantity;
    }
}
