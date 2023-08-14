package com.gugucon.shopping.pay.infrastructure;

import com.gugucon.shopping.pay.dto.PaySuccessParameter;
import com.gugucon.shopping.pay.infrastructure.dto.TossValidationRequest;
import com.gugucon.shopping.pay.infrastructure.dto.TossValidationResponse;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public final class TossPayValidator {

    private static final String VALIDATE_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    @Value("${toss.pay.secret-key}")
    private String secretKey;

    public TossPayValidator() {
        this.restTemplate = new RestTemplate();
        this.httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public void validatePayment(final PaySuccessParameter paySuccessParameter) {
        httpHeaders.setBasicAuth(Base64.getEncoder().encodeToString((secretKey + ":").getBytes()));
        final TossValidationRequest tossValidationRequest = TossValidationRequest.of(paySuccessParameter);
        final HttpEntity<TossValidationRequest> request = new HttpEntity<>(tossValidationRequest, httpHeaders);
        final ResponseEntity<TossValidationResponse> response = restTemplate.postForEntity(VALIDATE_URL,
                                                                                     request,
                                                                                     TossValidationResponse.class);
        validateSuccess(response);
    }

    private void validateSuccess(final ResponseEntity<TossValidationResponse> response) {
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException();
        }
        if (response.getBody() == null) {
            throw new RuntimeException();
        }
        if (!response.getBody().getStatus().equals("DONE")) {
            throw new RuntimeException();
        }
    }
}
