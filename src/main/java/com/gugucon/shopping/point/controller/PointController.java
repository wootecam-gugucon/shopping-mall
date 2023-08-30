package com.gugucon.shopping.point.controller;

import com.gugucon.shopping.auth.dto.MemberPrincipal;
import com.gugucon.shopping.point.dto.request.PointChargeRequest;
import com.gugucon.shopping.point.dto.response.PointResponse;
import com.gugucon.shopping.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/point")
public class PointController {

    private final PointService pointService;

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void charge(@RequestBody @Valid final PointChargeRequest pointChargeRequest,
                       @AuthenticationPrincipal final MemberPrincipal principal) {
        pointService.charge(pointChargeRequest, principal.getId());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PointResponse getCurrentPoint(@AuthenticationPrincipal final MemberPrincipal principal) {
        return pointService.getCurrentPoint(principal.getId());
    }
}
