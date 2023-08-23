package com.gugucon.shopping.pay.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.order.domain.entity.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("OrderIdBase64Translator 단위테스트")
class OrderIdBase64TranslatorTest {

    private final OrderIdTranslator orderIdTranslator = new OrderIdBase64Translator();

    @ParameterizedTest
    @ValueSource(strings = {"1", "주문 이름", "검정 하이엔드 골져스 원더풀 레저 스틸 가죽 자켓"})
    @DisplayName("인코딩한 내용을 디코딩하면 같은 내용이 된다")
    void encodeAndDecodeSuccess_SameString(String orderName) {
        // given
        Long orderId = 1_000_000L;


        // when
        String encodedString = orderIdTranslator.encode(orderId, orderName);
        Long decodeId = orderIdTranslator.decode(encodedString);

        // then
        assertThat(orderId).isEqualTo(decodeId);
    }

    @Test
    @DisplayName("디코딩 불가능한 문자열을 받으면 예외를 던진다")
    void decodeFail_NotAvailableString() {
        // given
        String notAvaliable = "not available";

        // when & then
        final ShoppingException exception = assertThrows(ShoppingException.class,
                                                         () -> orderIdTranslator.decode(notAvaliable));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNKNOWN_ERROR);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "주문 이름", "검정 하이엔드 골져스 원더풀 레저 스틸 가죽 자켓"})
    @DisplayName("인코딩 후 길이가 6자 이상 64자 이내이다.")
    void encodeSuccess_LimitFrom6To64(String orderName) {
        // given
        Long orderId = 1L;

        // when
        String encodedString = orderIdTranslator.encode(orderId, orderName);

        // then
        assertThat(encodedString).hasSizeBetween(6, 64);
    }
}
