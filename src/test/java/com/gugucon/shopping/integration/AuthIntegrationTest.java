package com.gugucon.shopping.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.TestUtils;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.member.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("jwt 토큰 인증 기능 통합 테스트")
class AuthIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("jwt 토큰 인증에 성공한다")
    void authenticate() {
        // given
        final String email = "test_email@woowafriends.com";
        final String password = "test_password!";
        final String nickname = "tester1";
        TestUtils.signup(new SignupRequest(email, password, password, nickname));
        String accessToken = TestUtils.login(new LoginRequest(email, password));

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/api/v1/order-history")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("jwt 토큰 정보가 유효하지 않으면 인증에 실패한다")
    void authenticateFail_jwtTokenNotExist() {
        // given
        final String invalidToken = "";
        final CartItemInsertRequest cartItemInsertRequest = new CartItemInsertRequest(1L);

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(invalidToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(cartItemInsertRequest)
            .when().post("/api/v1/cart/items")
            .then().log().all()
            .extract();

        /* then */
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LOGIN_REQUESTED);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.LOGIN_REQUESTED.getMessage());
    }
}
