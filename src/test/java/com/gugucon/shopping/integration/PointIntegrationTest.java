package com.gugucon.shopping.integration;

import static com.gugucon.shopping.utils.ApiUtils.chargePoint;
import static com.gugucon.shopping.utils.ApiUtils.loginAfterSignUp;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.point.dto.request.PointChargeRequest;
import com.gugucon.shopping.point.dto.response.PointResponse;
import com.gugucon.shopping.utils.ApiUtils;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@IntegrationTest
@DisplayName("포인트 기능 통합 테스트")
class PointIntegrationTest {

    @Test
    @DisplayName("포인트 충전을 요청하면 포인트를 충전한다.")
    void chargePointSuccess() {
        // given
        final String accessToken = loginAfterSignUp("test_email@test.com", "test_password!");
        final PointChargeRequest pointChargeRequest = new PointChargeRequest(1000L);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(pointChargeRequest)
                .when()
                .put("/api/v1/point")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    @DisplayName("0 혹은 음수 값으로 포인트 충전을 요청하면 400 상태코드를 응답한다.")
    void chargePointSuccess(Long point) {
        // given
        final String accessToken = loginAfterSignUp("test_email@test.com", "test_password!");
        final PointChargeRequest pointChargeRequest = new PointChargeRequest(point);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(pointChargeRequest)
                .when()
                .put("/api/v1/point")
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.POINT_CHARGE_NOT_POSITIVE);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(longs = {1000L, 2000L, 100000L, 100_000_000L})
    @DisplayName("포인트 잔액을 불러온다.")
    void getPoint(Long point) {
        // given
        final String accessToken = loginAfterSignUp("test_email@test.com", "test_password!");
        chargePoint(accessToken, point);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/point")
                .then().log().all()
                .extract();

        // then
        final PointResponse pointResponse = response.as(PointResponse.class);
        assertThat(pointResponse.getPoint()).isEqualTo(point);
    }
}
