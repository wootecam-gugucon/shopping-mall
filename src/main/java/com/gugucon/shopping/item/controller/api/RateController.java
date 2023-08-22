package com.gugucon.shopping.item.controller.api;

import com.gugucon.shopping.item.dto.request.RateCreateRequest;
import com.gugucon.shopping.item.dto.response.RateResponse;
import com.gugucon.shopping.item.service.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rate")
@RequiredArgsConstructor
public class RateController {

    private final RateService rateService;

    @PostMapping
    public ResponseEntity<Void> createRate(@AuthenticationPrincipal final Long memberId,
                                           @RequestBody final RateCreateRequest request) {
        rateService.createRate(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/product/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public RateResponse getRates(@PathVariable final Long productId) {
        return rateService.getRates(productId);
    }
}
