package shopping.cart.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import shopping.cart.domain.vo.Money;
import shopping.cart.domain.vo.Quantity;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue
    private Long id;
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private Money price;
    private String imageFileName;
    @Embedded
    private Quantity quantity;

    protected OrderItem() {
    }

    public OrderItem(final Long id, final Money price, final String imageFileName,
        final Quantity quantity) {
        this.id = id;
        this.price = price;
        this.imageFileName = imageFileName;
        this.quantity = quantity;
    }

    public OrderItem(final Money price, final String imageFileName, final Quantity quantity) {
        this.price = price;
        this.imageFileName = imageFileName;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Money getPrice() {
        return price;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public Quantity getQuantity() {
        return quantity;
    }
}
