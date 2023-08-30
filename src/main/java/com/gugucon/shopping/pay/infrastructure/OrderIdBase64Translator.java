package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public final class OrderIdBase64Translator implements OrderIdTranslator {

    private static final String DELIMITER = "--";
    private static final int MAX_LENGTH = 64;

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
        final String encoded = encoder.encodeToString(joined.getBytes());
        return limitLength(encoded);
    }

    private String limitLength(final String encoded) {
        if (encoded.length() > MAX_LENGTH) {
            return encoded.substring(0, MAX_LENGTH);
        }
        return encoded;
    }

    @Override
    public Long decode(final String encodedOrderId) {
        if (encodedOrderId == null) {
            throw new ShoppingException(ErrorCode.UNKNOWN_ERROR);
        }

        try {
            final String decoded = new String(decoder.decode(encodedOrderId.getBytes()));
            return Long.parseLong(decoded.split(DELIMITER)[0]);
        } catch (IllegalArgumentException e) {
            throw new ShoppingException(ErrorCode.UNKNOWN_ERROR);
        }
    }
}
