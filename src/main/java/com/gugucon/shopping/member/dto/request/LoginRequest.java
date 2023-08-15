package com.gugucon.shopping.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public final class LoginRequest {

    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
