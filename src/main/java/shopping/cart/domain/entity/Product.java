package shopping.cart.domain.entity;

import shopping.cart.domain.vo.WonMoney;

import javax.persistence.*;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String imageFileName;
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private WonMoney price;

    protected Product() {
    }

    public Product(final Long id, final String name, final String imageFileName,
                   final WonMoney price) {
        this.id = id;
        this.name = name;
        this.imageFileName = imageFileName;
        this.price = price;
    }

    public Product(final String name, final String imageFileName, final WonMoney price) {
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

    public WonMoney getPrice() {
        return price;
    }
}
