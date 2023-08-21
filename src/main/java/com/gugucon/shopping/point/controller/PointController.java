package com.gugucon.shopping.point.controller;

import com.gugucon.shopping.point.dto.request.PointChargeRequest;
import com.gugucon.shopping.point.dto.response.PointResponse;
import com.gugucon.shopping.point.service.PointService;
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
    public void charge(@RequestBody final PointChargeRequest pointChargeRequest,
                       @AuthenticationPrincipal final Long memberId) {
        pointService.charge(pointChargeRequest, memberId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PointResponse getCurrentPoint(@AuthenticationPrincipal final Long memberId) {
        return pointService.getCurrentPoint(memberId);
    }
}
