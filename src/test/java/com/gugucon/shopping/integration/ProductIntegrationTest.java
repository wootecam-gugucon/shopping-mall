package com.gugucon.shopping.integration;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.utils.DomainUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("상품 기능 통합 테스트")
class ProductIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

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

        final List<String> actualNames = products.stream()
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

    @Test
    @DisplayName("검색어로 검색한 결과를 최신순으로 정렬하여 반환한다")
    void searchProducts() {
        // given
        final List<Product> products = List.of(
                DomainUtils.createProductWithoutId("사과"), // O
                DomainUtils.createProductWithoutId("맛있는 사과"), // O
                DomainUtils.createProductWithoutId("사과는 맛있어"), // O
                DomainUtils.createProductWithoutId("가나다라마사과과"), // O
                DomainUtils.createProductWithoutId("가나다라마바사"), // X
                DomainUtils.createProductWithoutId("과놔돠롸") // X
        );
        final List<Product> persistProducts = productRepository.saveAll(products);

        final String keyword = "사과";

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .when().get("/api/v1/product/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<String> names = response.body()
                .jsonPath()
                .getList("contents", ProductResponse.class)
                .stream().map(ProductResponse::getName).toList();

        assertThat(names).containsExactly(
                "가나다라마사과과",
                "사과는 맛있어",
                "맛있는 사과",
                "사과"
        );

        productRepository.deleteAllInBatch(persistProducts);
    }

    @Test
    @DisplayName("검색어로 검색한 결과를 가격이 비싼순으로 정렬하여 반환한다")
    void searchProducts_sortByPriceDesc() {
        // given
        final List<Product> products = List.of(
                DomainUtils.createProductWithoutId("사과", 2500), // O
                DomainUtils.createProductWithoutId("맛있는 사과", 3000), // O
                DomainUtils.createProductWithoutId("사과는 맛있어", 1000), // O
                DomainUtils.createProductWithoutId("가나다라마사과과", 4000), // O
                DomainUtils.createProductWithoutId("가나다라마바사", 2000), // X
                DomainUtils.createProductWithoutId("과놔돠롸", 4500) // X
        );
        final List<Product> persistProducts = productRepository.saveAll(products);

        final String keyword = "사과";

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .queryParam("sort", "price,desc")
                .when().get("/api/v1/product/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<String> names = response.body()
                .jsonPath()
                .getList("contents", ProductResponse.class)
                .stream().map(ProductResponse::getName).toList();

        assertThat(names).containsExactly(
                "가나다라마사과과",
                "맛있는 사과",
                "사과",
                "사과는 맛있어"
        );

        productRepository.deleteAllInBatch(persistProducts);
    }

    @Test
    @DisplayName("검색어로 검색한 결과를 가격이 저렴한 순으로 정렬하여 반환한다")
    void searchProducts_sortByPriceAsc() {
        // given
        final List<Product> products = List.of(
                DomainUtils.createProductWithoutId("사과", 2500), // O
                DomainUtils.createProductWithoutId("맛있는 사과", 3000), // O
                DomainUtils.createProductWithoutId("사과는 맛있어", 1000), // O
                DomainUtils.createProductWithoutId("가나다라마사과과", 4000), // O
                DomainUtils.createProductWithoutId("가나다라마바사", 2000), // X
                DomainUtils.createProductWithoutId("과놔돠롸", 4500) // X
        );
        final List<Product> persistProducts = productRepository.saveAll(products);

        final String keyword = "사과";

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .queryParam("sort", "price,asc")
                .when().get("/api/v1/product/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<String> names = response.body()
                .jsonPath()
                .getList("contents", ProductResponse.class)
                .stream().map(ProductResponse::getName).toList();

        assertThat(names).containsExactly(
                "사과는 맛있어",
                "사과",
                "맛있는 사과",
                "가나다라마사과과"
        );

        productRepository.deleteAllInBatch(persistProducts);
    }

    @Test
    @DisplayName("빈 문자열의 키워드로 검색하면 에러를 반환한다")
    void searchProducts_emptyKeyword() {
        // given
        final List<Product> products = List.of(
                DomainUtils.createProductWithoutId("사과", 2500), // O
                DomainUtils.createProductWithoutId("맛있는 사과", 3000), // O
                DomainUtils.createProductWithoutId("사과는 맛있어", 1000), // O
                DomainUtils.createProductWithoutId("가나다라마사과과", 4000), // O
                DomainUtils.createProductWithoutId("가나다라마바사", 2000), // X
                DomainUtils.createProductWithoutId("과놔돠롸", 4500) // X
        );
        final List<Product> persistProducts = productRepository.saveAll(products);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", "")
                .when().get("/api/v1/product/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMPTY_INPUT);

        productRepository.deleteAllInBatch(persistProducts);
    }

    @Test
    @DisplayName("키워드 없이 검색하면 에러를 반환한다")
    void searchProducts_noKeyword() {
        // given
        final List<Product> products = List.of(
                DomainUtils.createProductWithoutId("사과", 2500), // O
                DomainUtils.createProductWithoutId("맛있는 사과", 3000), // O
                DomainUtils.createProductWithoutId("사과는 맛있어", 1000), // O
                DomainUtils.createProductWithoutId("가나다라마사과과", 4000), // O
                DomainUtils.createProductWithoutId("가나다라마바사", 2000), // X
                DomainUtils.createProductWithoutId("과놔돠롸", 4500) // X
        );
        final List<Product> persistProducts = productRepository.saveAll(products);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/api/v1/product/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMPTY_INPUT);

        productRepository.deleteAllInBatch(persistProducts);
    }
}
