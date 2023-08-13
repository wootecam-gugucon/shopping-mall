package com.gugucon.shopping.member.controlller;

import com.gugucon.shopping.member.controlller.validator.LoginRequestValidator;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.response.LoginResponse;
import com.gugucon.shopping.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
public class LoginController {

    private final MemberService memberService;
    private final LoginRequestValidator loginRequestValidator;

    @InitBinder
    public void init(final WebDataBinder dataBinder) {
        dataBinder.addValidators(loginRequestValidator);
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody @Validated final LoginRequest loginRequest) {
        return memberService.login(loginRequest);
    }
}
