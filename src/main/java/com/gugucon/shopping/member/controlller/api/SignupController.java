package com.gugucon.shopping.member.controlller.api;

import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/signup")
public class SignupController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void signupMember(@RequestBody @Valid SignupRequest signupRequest) {
        memberService.signup(signupRequest);
    }
}
