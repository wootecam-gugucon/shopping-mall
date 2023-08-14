package com.gugucon.shopping.pay.infrastructure;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public final class OrderIdBase64Translator implements OrderIdTranslator {

    private static final String DELIMITER = "--";

    private final Encoder encoder;
    private final Decoder decoder;

    public OrderIdBase64Translator() {
        this.encoder = Base64.getEncoder();
        this.decoder = Base64.getDecoder();
    }

    @Override
    public String encode(final Long orderId, final String orderName) {
        final String joinned = String.join(DELIMITER, orderId.toString(), orderName, LocalDateTime.now().toString());
        return encoder.encodeToString(joinned.getBytes());
    }

    @Override
    public Long decode(final String encodedOrderId) {
        String decoded = new String(decoder.decode(encodedOrderId.getBytes()));
        return Long.parseLong(decoded.split(DELIMITER)[0]);
    }
}
