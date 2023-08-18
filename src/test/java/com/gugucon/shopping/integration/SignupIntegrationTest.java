package com.gugucon.shopping.integration;

import com.gugucon.shopping.TestUtils;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.member.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("회원 가입 기능 통합 테스트")
class SignupIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입한다.")
    void signup() {
        /* given */
        final String email = "test_email@test.com";
        final String password = "test_password!";
        final String nickname = "김동주";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signupRequest)
                .when().post("/api/v1/signup")
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 회원가입을 요청했을 때 400 상태코드를 응답한다.")
    void signupFail_existEmail() {
        /* given */
        final String email = "test_email@test.com";
        final String password = "test_password!";
        final String nickname = "김동주";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);
        TestUtils.signup(signupRequest);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signupRequest)
                .when().post("/api/v1/signup")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXIST);
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 다르면 회원가입을 요청했을 때 400 상태코드를 응답한다.")
    void signupFail_differentPassword() {
        /* given */
        final String email = "test_email@test.com";
        final String password = "test_password!";
        final String passwordCheck = "not_same_password";
        final String nickname = "김동주";
        final SignupRequest signupRequest = new SignupRequest(email, password, passwordCheck, nickname);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signupRequest)
                .when().post("/api/v1/signup")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_CHECK_NOT_SAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Qwerty!!@gmail.com", "Qwerty@@gmail.com", "Qwerty@gmail", "Qwerty@gmail.c", "asdf"})
    @DisplayName("이메일 형식이 올바르지 않으면 회원가입을 요청했을 때 400 상태코드를 응답한다.")
    void signupFail_invalidEmail(final String email) {
        /* given */
        final String password = "test_password!";
        final String nickname = "김동주";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signupRequest)
                .when().post("/api/v1/signup")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMAIL_PATTERN);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asd", "123!!", "qwerty_qwerty_qwerty_"})
    @DisplayName("비밀번호 형식이 올바르지 않으면 회원가입을 요청했을 때 400 상태코드를 응답한다.")
    void signupFail_invalidPassword(final String password) {
        /* given */
        final String email = "test_email@test.com";
        final String nickname = "김동주";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signupRequest)
                .when().post("/api/v1/signup")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD_PATTERN);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이메일 정보가 없으면 회원가입을 요청했을 때 400 상태코드를 응답한다.")
    void signupFail_withoutEmail(final String email) {
        /* given */
        final String password = "test_password!";
        final String nickname = "김동주";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signupRequest)
                .when().post("/api/v1/signup")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD_MISSING);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("비밀번호 정보가 없으면 회원가입을 요청했을 때 400 상태코드를 응답한다.")
    void signupFail_withoutPassword(final String password) {
        /* given */
        final String email = "test_email@test.com";
        final String nickname = "김동주";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signupRequest)
                .when().post("/api/v1/signup")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD_MISSING);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("닉네임 정보가 없으면 회원가입을 요청했을 때 400 상태코드를 응답한다.")
    void signupFail_withoutNickname(final String nickname) {
        /* given */
        final String email = "test_email@test.com";
        final String password = "test_password!";
        final SignupRequest signupRequest = new SignupRequest(email, password, password, nickname);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signupRequest)
                .when().post("/api/v1/signup")
                .then().log().all()
                .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD_MISSING);
    }
}
