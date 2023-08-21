package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.pay.dto.request.PayValidationRequest;
import com.gugucon.shopping.pay.infrastructure.dto.TossValidationRequest;
import com.gugucon.shopping.pay.infrastructure.dto.TossValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Base64.Encoder;

@Slf4j
public final class TossPayValidator implements PayValidator {

    private static final String VALIDATE_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String BASIC_AUTH_DELIMITER = ":";

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    public TossPayValidator(final RestTemplate restTemplate, final String secretKey) {
        this.restTemplate = restTemplate;
        this.httpHeaders = new HttpHeaders();
        setHeaderForConnect(secretKey);
    }

    private void setHeaderForConnect(final String secretKey) {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Encoder encoder = Base64.getEncoder();
        httpHeaders.setBasicAuth(encoder.encodeToString((secretKey + BASIC_AUTH_DELIMITER).getBytes()));
    }

    @Override
    public void validatePayment(final PayValidationRequest payValidationRequest) {
        final TossValidationRequest tossValidationRequest = TossValidationRequest.of(payValidationRequest);
        final HttpEntity<TossValidationRequest> request = new HttpEntity<>(tossValidationRequest, httpHeaders);
        log.info("toss validation request sent for orderId: {}", payValidationRequest.getOrderId());
        final ResponseEntity<TossValidationResponse> response = restTemplate.postForEntity(VALIDATE_URL,
                                                                                           request,
                                                                                           TossValidationResponse.class);
        validateSuccess(response);
    }

    private void validateSuccess(final ResponseEntity<TossValidationResponse> response) {
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ShoppingException(ErrorCode.UNKNOWN_ERROR);
        }
        if (response.getBody() == null) {
            throw new ShoppingException(ErrorCode.UNKNOWN_ERROR);
        }
        if (!response.getBody().getStatus().equals("DONE")) {
            throw new ShoppingException(ErrorCode.UNKNOWN_ERROR);
        }
    }
}
