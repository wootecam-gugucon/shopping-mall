package com.gugucon.shopping.member.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class LoginResponse {

    private String accessToken;

    public static LoginResponse from(final String accessToken) {
        return new LoginResponse(accessToken);
    }
}
