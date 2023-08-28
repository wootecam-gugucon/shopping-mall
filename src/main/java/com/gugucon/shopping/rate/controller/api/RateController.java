package com.gugucon.shopping.rate.controller.api;

import com.gugucon.shopping.auth.dto.MemberPrincipal;
import com.gugucon.shopping.rate.dto.request.RateCreateRequest;
import com.gugucon.shopping.rate.dto.response.RateDetailResponse;
import com.gugucon.shopping.rate.dto.response.RateResponse;
import com.gugucon.shopping.rate.service.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rate")
@RequiredArgsConstructor
public class RateController {

    private final RateService rateService;

    @PostMapping
    public ResponseEntity<Void> createRate(@RequestBody final RateCreateRequest request,
                                           @AuthenticationPrincipal final MemberPrincipal principal) {
        rateService.createRate(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/orderItem/{orderItemId}")
    @ResponseStatus(HttpStatus.OK)
    public RateDetailResponse getRateDetail(@PathVariable final long orderItemId,
                                            @AuthenticationPrincipal final MemberPrincipal principal) {
        return rateService.getRateDetail(principal.getId(), orderItemId);
    }

    @GetMapping("/product/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public RateResponse getRates(@PathVariable final Long productId) {
        return rateService.getRates(productId);
    }

    @GetMapping("/product/{productId}/custom")
    @ResponseStatus(HttpStatus.OK)
    public RateResponse getCustomRate(@PathVariable final Long productId,
                                      @AuthenticationPrincipal final MemberPrincipal principal) {
        return rateService.getCustomRate(productId, principal);
    }
}
