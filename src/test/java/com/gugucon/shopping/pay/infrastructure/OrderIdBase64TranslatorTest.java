package com.gugucon.shopping.pay.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@DisplayName("OrderIdBase64Translator 단위테스트")
class OrderIdBase64TranslatorTest {

    private final OrderIdTranslator orderIdTranslator = new OrderIdBase64Translator();

    //@Test
    @DisplayName("인코딩한 내용을 디코딩하면 같은 내용이 된다")
    void encodeAndDecodeSuccess_SameString() {
        // given
        Long orderId = 1L;
        String orderName = "주문 이름";

        // when
        //String encodedString = orderIdTranslator.encode(orderId, orderName);
        //Long decodeId = orderIdTranslator.decode(encodedString);

        // then
        //assertThat(orderId).isEqualTo(decodeId);
    }

    @Test
    @DisplayName("디코딩 불가능한 문자열을 받으면 예외를 던진다")
    void decodeFail_NotAvailableString() {
        // given
        String notAvaliable = "not available";

        // when
        Exception exception = catchException(() -> orderIdTranslator.decode(notAvaliable));

        // then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
