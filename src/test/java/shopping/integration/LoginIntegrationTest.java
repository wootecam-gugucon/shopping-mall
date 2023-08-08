package shopping.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import shopping.auth.dto.request.LoginRequest;
import shopping.auth.dto.response.LoginResponse;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ErrorResponse;

@DisplayName("로그인 기능 통합 테스트")
class LoginIntegrationTest extends IntegrationTest {

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("로그인에 성공한다.")
    void loginSuccess() {
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
    @DisplayName("존재하지 않는 이메일로 로그인을 시도한다.")
    void loginWithNotRegisteredEmail() {
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
    @DisplayName("틀린 비밀번호로 로그인을 시도한다.")
    void loginWithIncorrectPassword() {
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
    @DisplayName("이메일 정보 없이 로그인을 시도한다.")
    void loginWithoutEmail(final String email) {
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
    @DisplayName("비밀번호 정보 없이 로그인을 시도한다.")
    void loginWithoutPassword(final String password) {
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
