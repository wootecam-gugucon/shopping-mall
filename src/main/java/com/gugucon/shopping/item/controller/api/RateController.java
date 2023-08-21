package com.gugucon.shopping.item.controller.api;

import com.gugucon.shopping.item.dto.request.RateCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rate")
public class RateController {

    @PostMapping
    public ResponseEntity<Void> createRate(@RequestBody RateCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
