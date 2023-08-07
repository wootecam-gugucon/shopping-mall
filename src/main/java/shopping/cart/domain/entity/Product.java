package shopping.cart.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import shopping.cart.domain.vo.Money;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String imageFileName;
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private Money price;

    protected Product() {
    }

    public Product(final Long id, final String name, final String imageFileName,
        final Money price) {
        this.id = id;
        this.name = name;
        this.imageFileName = imageFileName;
        this.price = price;
    }

    public Product(final String name, final String imageFileName, final Money price) {
        this(null, name, imageFileName, price);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public Money getPrice() {
        return price;
    }
}
