package com.gugucon.shopping.pay.domain;

import com.gugucon.shopping.pay.dto.PayResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Base64;

@Entity
public final class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String encodedOrderId;

    private String orderName;

    private Long price;

    public Pay() {
    }

    private Pay(final Long id, final Long orderId, final String orderName, final Long price) {
        this.id = id;
        this.orderId = orderId;
        this.orderName = orderName;
        this.encodedOrderId = Base64.getEncoder().encodeToString((orderId + orderName).getBytes());
        this.price = price;
    }

    public Pay(final Long orderId, final String orderName, final Long price) {
        this(null, orderId, orderName, price);
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getPrice() {
        return price;
    }

    public PayResponse toPayResponse() {
        return new PayResponse(encodedOrderId, orderName);
    }

    public void validateMoney(int price) {
        if (this.price == price) {
            throw new RuntimeException();
        }
    }
}
