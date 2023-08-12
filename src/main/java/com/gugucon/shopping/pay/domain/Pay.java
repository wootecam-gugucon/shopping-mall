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

    private Long price;

    public Pay() {
    }

    private Pay(final Long id, final Long orderId, final Long price) {
        this.id = id;
        this.orderId = orderId;
        this.price = price;
    }

    public Pay(final Long orderId, final Long price) {
        this(null, orderId, price);
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
        // TODO: 주문 이름 가져오기
        final String orderName = "대충 주문 이름";
        final String encodedOrderId = Base64.getEncoder().encodeToString((orderId + orderName).getBytes());
        return new PayResponse(encodedOrderId, orderName);
    }
}
