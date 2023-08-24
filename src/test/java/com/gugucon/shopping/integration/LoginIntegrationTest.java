package com.gugucon.shopping.integration;

import static com.gugucon.shopping.utils.ApiUtils.signUp;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
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

@IntegrationTest
@DisplayName("로그인 기능 통합 테스트")
class LoginIntegrationTest {

    @Test
    @DisplayName("로그인한다.")
    void login() {
        /* given */
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        signUp(email, password);
        LoginRequest loginRequest = new LoginRequest(email, password);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login")
                .then().log().all()
                .extract();

        /* then */
        final LoginResponse loginResponse = response.as(LoginResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(loginResponse.getAccessToken()).isNotBlank();
    }

    @Test
    @DisplayName("존재하지 않는 이메일이면 로그인을 요청했을 때 400 상태코드를 응답한다.")
    void loginFail_invalidEmail() {
        /* given */
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        LoginRequest loginRequest = new LoginRequest(email, password);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMAIL_NOT_REGISTERED);
    }

    @Test
    @DisplayName("틀린 비밀번호이면 로그인을 요청했을 때 400 상태코드를 응답한다.")
    void loginFail_incorrectPassword() {
        /* given */
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        signUp(email, password);
        LoginRequest loginRequest = new LoginRequest(email, "invalid_password");

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_NOT_CORRECT);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이메일 정보가 없으면 로그인을 요청했을 때 400 상태코드를 응답한다.")
    void loginFail_withoutEmail(final String loginEmail) {
        /* given */
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        signUp(email, password);
        LoginRequest loginRequest = new LoginRequest(loginEmail, password);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD_MISSING);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("비밀번호 정보가 없으면 로그인을 요청했을 때 400 상태코드를 응답한다.")
    void loginFail_withoutPassword(final String loginPassword) {
        /* given */
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        signUp(email, password);
        LoginRequest loginRequest = new LoginRequest(email, loginPassword);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/v1/login")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD_MISSING);
    }
}
