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
        final String joined = String.join(DELIMITER,
                                           String.valueOf(orderId),
                                           orderName,
                                           String.valueOf(LocalDateTime.now()));
        return encoder.encodeToString(joined.getBytes());
    }

    @Override
    public Long decode(final String encodedOrderId) {
        String decoded = new String(decoder.decode(encodedOrderId.getBytes()));
        try{
            return Long.parseLong(decoded.split(DELIMITER)[0]);
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }
    }
}
