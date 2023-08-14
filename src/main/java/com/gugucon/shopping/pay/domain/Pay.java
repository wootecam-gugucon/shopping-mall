package com.gugucon.shopping.pay.domain;

import com.gugucon.shopping.pay.dto.PayResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Base64;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String encodedOrderId;

    private String orderName;

    private Long price;

    public Pay(final Long orderId, final String orderName, final Long price) {
        this(null, orderId, Base64.getEncoder().encodeToString((orderId + orderName).getBytes()), orderName, price);
    }

    public PayResponse toPayResponse() {
        return new PayResponse(encodedOrderId, orderName);
    }

    public void validateMoney(final int price) {
        if (this.price != price) {
            throw new RuntimeException();
        }
    }
}
