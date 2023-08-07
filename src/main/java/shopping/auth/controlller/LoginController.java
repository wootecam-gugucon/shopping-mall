package shopping.auth.controlller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.controlller.validator.LoginRequestValidator;
import shopping.auth.dto.request.LoginRequest;
import shopping.auth.dto.response.LoginResponse;
import shopping.auth.service.UserService;

@RestController
@RequestMapping("/login")
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
