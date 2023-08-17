package com.gugucon.shopping.integration;

import com.gugucon.shopping.integration.config.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("상품 기능 통합 테스트")
class ProductIntegrationTest {

    @Test
    @DisplayName("상품 전체 목록을 조회한다.")
    void readAllProducts() {
        /* given */

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/")
                .then().log().all()
                .extract();

        /* then */
        final List<String> result = response.htmlPath()
                .getList("html.body.div.section.div.div.div.span");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(result).containsAll(List.of("치킨", "피자", "사케"));
    }
}
