package com.gugucon.shopping.integration;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.response.LoginResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("로그인 기능 통합 테스트")
class LoginIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("로그인한다.")
    void login() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
                "test_password!");

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login/token")
                .then().log().all()
                .extract();

        /* then */
        final LoginResponse loginResponse = response.as(LoginResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(loginResponse.getAccessToken()).isNotBlank();
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인할 수 없다.")
    void loginFail_invalidEmail() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("unregistered_email@gmail.com",
                "test_password!");

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login/token")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMAIL_NOT_REGISTERED);
    }

    @Test
    @DisplayName("틀린 비밀번호로 로그인할 수 없다.")
    void loginFail_incorrectPassword() {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com",
                "invalid_password");

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login/token")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_NOT_CORRECT);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이메일 없이 로그인할 수 없다.")
    void loginFail_withoutEmail(final String email) {
        /* given */
        final LoginRequest loginRequest = new LoginRequest(email, "test_password!");

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login/token")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD_MISSING);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("비밀번호 없이 로그인할 수 없다.")
    void loginFail_withoutPassword(final String password) {
        /* given */
        final LoginRequest loginRequest = new LoginRequest("test_email@woowafriends.com", password);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login/token")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD_MISSING);
    }
}
