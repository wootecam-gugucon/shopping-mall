package com.gugucon.shopping.integration;

import static com.gugucon.shopping.TestUtils.login;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

@DisplayName("상품 기능 통합 테스트")
class ProductIntegrationTest extends IntegrationTest {

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

    @Test
    @DisplayName("상품 전체 목록을 페이징하여 조회한다.")
    void readAllProducts_paging() {
        /* given */
        String accessToken = login(new LoginRequest("test_email@woowafriends.com", "test_password!"));
        PageRequest pageRequest = PageRequest.of(0, 20);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .body(pageRequest)
            .contentType(ContentType.JSON)
            .auth().oauth2(accessToken)
            .when().get("/api/v1/product")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<ProductResponse> result = response.body()
            .jsonPath()
            .getList(".data", ProductResponse.class);
        final boolean isLastPage = response.body()
            .jsonPath()
            .getBoolean(".isLastPage");
        assertThat(result).hasSize(pageRequest.getPageSize());
        assertThat(isLastPage).isFalse();
    }
}
