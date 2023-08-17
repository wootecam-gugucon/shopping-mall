package com.gugucon.shopping.integration;

import static com.gugucon.shopping.TestUtils.login;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.common.dto.response.PagedResponse;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @DisplayName("페이징 조건이 기재되지 않으면 기본 설정에 따라 페이징하여 반환한다.")
    void readAllProducts_defaultPaging() {
        /* given */
        String accessToken = login(new LoginRequest("test_email@woowafriends.com", "test_password!"));

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().get("/api/v1/product")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final PagedResponse result = response.as(PagedResponse.class);
        final List<ProductResponse> products = result.getData();
        final int totalPage = result.getTotalPage();

        assertThat(products).hasSize(3);
        assertThat(totalPage).isEqualTo(1);
    }

    @Test
    @DisplayName("입력된 페이징 조건에 따라 페이징하여 반환한다.")
    void readAllProducts_paging() {
        /* given */
        String accessToken = login(new LoginRequest("test_email@woowafriends.com", "test_password!"));

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().get("/api/v1/product?page=0&size=1")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final PagedResponse result = response.as(PagedResponse.class);
        final List<ProductResponse> products = result.getData();
        final int totalPage = result.getTotalPage();

        assertThat(products).hasSize(1);
        assertThat(totalPage).isEqualTo(3);
    }

    @Test
    @DisplayName("정렬 조건이 기재되지 않은 경우 최신순으로 정렬하여 반환한다")
    void readAllProducts_defaultSorting() {
        /* given */
        String accessToken = login(new LoginRequest("test_email@woowafriends.com", "test_password!"));

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().get("/api/v1/product")
            .then().contentType(ContentType.JSON).log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<ProductResponse> products = response.body()
            .jsonPath()
            .getList("data", ProductResponse.class);
        final List<ProductResponse> sortedProducts = products.stream()
            .sorted(Comparator.comparing(ProductResponse::getId).reversed())
            .toList();

        assertThat(products).containsExactlyElementsOf(sortedProducts);
    }
}
