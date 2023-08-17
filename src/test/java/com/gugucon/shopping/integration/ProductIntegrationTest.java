package com.gugucon.shopping.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@IntegrationTest
@DisplayName("상품 기능 통합 테스트")
class ProductIntegrationTest {

    @Test
    @DisplayName("페이징 조건이 기재되지 않으면 기본 설정 (page=0, size=20) 에 따라 페이징하여 반환한다.")
    void readAllProducts_defaultPaging() {
        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/api/v1/product")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final JsonPath result = response.body().jsonPath();
        final List<ProductResponse> products = result.getList("contents", ProductResponse.class);
        final int totalPage = result.getInt("totalPage");
        final int currentPage = result.getInt("currentPage");
        final int size = result.getInt("size");

        List<String> actualNames = products.stream()
            .map(ProductResponse::getName)
            .toList();
        assertThat(actualNames).containsExactly("품절된 치킨", "사케", "피자", "치킨");
        assertThat(currentPage).isZero();
        assertThat(size).isEqualTo(20);
        assertThat(totalPage).isEqualTo(1);
    }

    @Test
    @DisplayName("입력된 페이징 조건에 따라 페이징하여 반환한다.")
    void readAllProducts_paging() {
        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/api/v1/product?page=0&size=1")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final JsonPath result = response.body().jsonPath();
        final List<ProductResponse> products = result.getList("contents", ProductResponse.class);
        final int totalPage = result.getInt("totalPage");
        final int currentPage = result.getInt("currentPage");
        final int size = result.getInt("size");

        assertThat(products.get(0).getName()).isEqualTo("품절된 치킨");
        assertThat(totalPage).isEqualTo(4);
        assertThat(currentPage).isZero();
        assertThat(size).isEqualTo(1);
    }

    @Test
    @DisplayName("정렬 조건이 기재되지 않은 경우 최신순으로 정렬하여 반환한다")
    void readAllProducts_defaultSorting() {
        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/api/v1/product")
            .then().contentType(ContentType.JSON).log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<ProductResponse> products = response.body()
            .jsonPath()
            .getList("contents", ProductResponse.class);
        final List<ProductResponse> sortedProducts = products.stream()
            .sorted(Comparator.comparing(ProductResponse::getId).reversed())
            .toList();

        assertThat(products).containsExactlyElementsOf(sortedProducts);
    }
}
