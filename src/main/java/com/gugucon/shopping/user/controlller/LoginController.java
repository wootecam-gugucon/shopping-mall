package com.gugucon.shopping.user.controlller;

import com.gugucon.shopping.user.dto.request.LoginRequest;
import com.gugucon.shopping.user.dto.response.LoginResponse;
import com.gugucon.shopping.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.gugucon.shopping.user.controlller.validator.LoginRequestValidator;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController {

    private final UserService userService;
    private final LoginRequestValidator loginRequestValidator;

    @InitBinder
    public void init(final WebDataBinder dataBinder) {
        dataBinder.addValidators(loginRequestValidator);
    }

    public LoginController(final UserService userService,
        final LoginRequestValidator loginRequestValidator) {
        this.userService = userService;
        this.loginRequestValidator = loginRequestValidator;
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody @Validated LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }
}
