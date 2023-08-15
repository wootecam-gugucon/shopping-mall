package com.gugucon.shopping.member.controlller;

import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.response.LoginResponse;
import com.gugucon.shopping.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
public class LoginController {

    private final MemberService memberService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody @Valid final LoginRequest loginRequest) {
        return memberService.login(loginRequest);
    }
}
