package com.gugucon.shopping.member.controlller.validator;

import com.gugucon.shopping.member.dto.request.LoginRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class LoginRequestValidator implements Validator {

    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_PASSWORD = "password";
    private static final String CODE_REQUIRED = "required";

    @Override
    public boolean supports(final Class<?> clazz) {
        return LoginRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, FIELD_EMAIL, CODE_REQUIRED,
            new Object[]{FIELD_EMAIL});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, FIELD_PASSWORD, CODE_REQUIRED,
            new Object[]{FIELD_PASSWORD});
    }
}
