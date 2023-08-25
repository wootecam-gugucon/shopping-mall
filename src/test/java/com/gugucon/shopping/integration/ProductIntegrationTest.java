package com.gugucon.shopping.integration;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.dto.response.ProductDetailResponse;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.order.dto.response.OrderDetailResponse;
import com.gugucon.shopping.order.dto.response.OrderItemResponse;
import com.gugucon.shopping.rate.dto.request.RateCreateRequest;
import com.gugucon.shopping.utils.ApiUtils;
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

import static com.gugucon.shopping.utils.ApiUtils.buyAllProductsByPoint;
import static com.gugucon.shopping.utils.ApiUtils.createRateToOrderedItem;
import static com.gugucon.shopping.utils.ApiUtils.loginAfterSignUp;
import static com.gugucon.shopping.utils.ApiUtils.placeOrder;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("상품 기능 통합 테스트")
class ProductIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("페이징 조건이 기재되지 않으면 기본 설정 (page=0, size=20) 에 따라 페이징하여 반환한다.")
    void readAllProducts_defaultPaging() {
        /* given */
        insertAllProducts(List.of("품절된 치킨", "사케", "피자", "치킨"));

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/api/v1/products")
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
        assertThat(actualNames).containsExactly("치킨", "피자", "사케", "품절된 치킨");
        assertThat(currentPage).isZero();
        assertThat(size).isEqualTo(20);
        assertThat(totalPage).isEqualTo(1);
    }

    @Test
    @DisplayName("입력된 페이징 조건에 따라 페이징하여 반환한다.")
    void readAllProducts_paging() {
        /* given */
        insertAllProducts(List.of("품절된 치킨", "사케", "피자", "치킨"));

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/api/v1/products?page=0&size=1")
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final JsonPath result = response.body().jsonPath();
        final List<ProductResponse> products = result.getList("contents", ProductResponse.class);
        final int totalPage = result.getInt("totalPage");
        final int currentPage = result.getInt("currentPage");
        final int size = result.getInt("size");

        assertThat(products.get(0).getName()).isEqualTo("치킨");
        assertThat(totalPage).isEqualTo(4);
        assertThat(currentPage).isZero();
        assertThat(size).isEqualTo(1);
    }

    @Test
    @DisplayName("정렬 조건이 기재되지 않은 경우 최신순으로 정렬하여 반환한다")
    void readAllProducts_defaultSorting() {
        /* given */
        insertAllProducts(List.of("품절된 치킨", "사케", "피자", "치킨"));

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/api/v1/products")
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
        insertProduct("사과", 10);    // O
        insertProduct("맛있는 사과", 10);    // O
        insertProduct("사과는 맛있어", 10);    // O
        insertProduct("가나다라마사과과", 10);    // O
        insertProduct("가나다라마바사", 10);    // X
        insertProduct("과놔돠롸", 10);    // X

        final String keyword = "사과";

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .queryParam("sort", "createdAt,desc")
                .when().get("/api/v1/products/search")
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
    }

    @Test
    @DisplayName("검색어로 검색한 결과를 가격이 비싼순으로 정렬하여 반환한다")
    void searchProducts_sortByPriceDesc() {
        // given
        insertProduct("사과", 2500);    // O
        insertProduct("맛있는 사과", 3000);    // O
        insertProduct("사과는 맛있어", 1000);    // O
        insertProduct("가나다라마사과과", 4000);    // O
        insertProduct("가나다라마바사", 2000);    // X
        insertProduct("과놔돠롸", 4500);    // X

        final String keyword = "사과";

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .queryParam("sort", "price,desc")
                .when().get("/api/v1/products/search")
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
    }

    @Test
    @DisplayName("검색어로 검색한 결과를 가격이 저렴한 순으로 정렬하여 반환한다")
    void searchProducts_sortByPriceAsc() {
        // given
        insertProduct("사과", 2500);    // O
        insertProduct("맛있는 사과", 3000);    // O
        insertProduct("사과는 맛있어", 1000);    // O
        insertProduct("가나다라마사과과", 4000);    // O
        insertProduct("가나다라마바사", 2000);    // X
        insertProduct("과놔돠롸", 4500);    // X

        final String keyword = "사과";

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .queryParam("sort", "price,asc")
                .when().get("/api/v1/products/search")
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
    }

    @Test
    @DisplayName("검색어로 검색한 결과를 주문이 많은 순으로 정렬하여 반환한다")
    void searchProducts_sortByOrderDesc() {
        // given
        final Long 사과_id = insertProduct("사과", 2500);// O
        final Long 맛있는사과_id = insertProduct("맛있는 사과", 3000);    // O
        final Long 사과는맛있어_id = insertProduct("사과는 맛있어", 1000);    // O
        final Long 가나다라마사과과_id = insertProduct("가나다라마사과과", 4000);    // O
        final Long 가나다라마바사_id = insertProduct("가나다라마바사", 2000);    // X
        final Long 과놔돠롸_id = insertProduct("과놔돠롸", 4500);    // X

        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        ApiUtils.insertCartItem(accessToken, new CartItemInsertRequest(사과_id));
        ApiUtils.insertCartItem(accessToken, new CartItemInsertRequest(사과는맛있어_id));
        ApiUtils.insertCartItem(accessToken, new CartItemInsertRequest(가나다라마사과과_id));
        ApiUtils.insertCartItem(accessToken, new CartItemInsertRequest(맛있는사과_id));

        final List<CartItemResponse> cartItemResponses = ApiUtils.readCartItems(accessToken);
        final Long 사과_장바구니_id = cartItemResponses.get(0).getCartItemId();
        final Long 사과는맛있어_장바구니_id = cartItemResponses.get(1).getCartItemId();
        final Long 가나다라마사과과_장바구니_id = cartItemResponses.get(2).getCartItemId();
        final Long 맛있는사과_장바구니_id = cartItemResponses.get(3).getCartItemId();

        ApiUtils.updateCartItem(accessToken, 사과_장바구니_id, new CartItemUpdateRequest(10));
        ApiUtils.updateCartItem(accessToken, 사과는맛있어_장바구니_id, new CartItemUpdateRequest(9));
        ApiUtils.updateCartItem(accessToken, 가나다라마사과과_장바구니_id, new CartItemUpdateRequest(8));
        ApiUtils.updateCartItem(accessToken, 맛있는사과_장바구니_id, new CartItemUpdateRequest(7));

        placeOrder(accessToken);

        final String keyword = "사과";

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .queryParam("sort", "orderCount,desc")
                .when().get("/api/v1/products/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<String> names = response.body()
                .jsonPath()
                .getList("contents", ProductResponse.class)
                .stream().map(ProductResponse::getName).toList();

        assertThat(names).containsExactly(
                "사과",
                "사과는 맛있어",
                "가나다라마사과과",
                "맛있는 사과"
        );
    }

    @Test
    @DisplayName("검색어로 검색한 결과를 별점이 높은 순으로 정렬하여 반환한다")
    void searchProducts_sortByRateDesc() {
        // given
        final Long 사과_id = insertProduct("사과", 2500);// O
        final Long 맛있는사과_id = insertProduct("맛있는 사과", 3000);    // O
        final Long 사과는맛있어_id = insertProduct("사과는 맛있어", 1000);    // O
        final Long 가나다라마사과과_id = insertProduct("가나다라마사과과", 4000);    // O
        final Long 가나다라마바사_id = insertProduct("가나다라마바사", 2000);    // X
        final Long 과놔돠롸_id = insertProduct("과놔돠롸", 4500);    // X
        final List<Long> productIds = List.of(사과_id, 맛있는사과_id, 사과는맛있어_id, 가나다라마사과과_id);

        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final OrderDetailResponse orderDetailResponse = buyAllProductsByPoint(accessToken, productIds, 17000L);
        final List<OrderItemResponse> orderItemResponses = orderDetailResponse.getOrderItems();
        final Long 사과_주문_id = orderItemResponses.get(0).getId();
        final Long 맛있는사과_주문_id = orderItemResponses.get(1).getId();
        final Long 사과는맛있어_주문_id = orderItemResponses.get(2).getId();
        final Long 가나다라마사과과_주문_id = orderItemResponses.get(3).getId();

        createRateToOrderedItem(accessToken, new RateCreateRequest(사과_주문_id, (short) 1));
        createRateToOrderedItem(accessToken, new RateCreateRequest(사과는맛있어_주문_id, (short) 2));
        createRateToOrderedItem(accessToken, new RateCreateRequest(가나다라마사과과_주문_id, (short) 3));
        createRateToOrderedItem(accessToken, new RateCreateRequest(맛있는사과_주문_id, (short) 4));

        final String keyword = "사과";

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .queryParam("sort", "rate,desc")
                .when().get("/api/v1/products/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<String> names = response.body()
                                           .jsonPath()
                                           .getList("contents", ProductResponse.class)
                                           .stream().map(ProductResponse::getName).toList();

        assertThat(names).containsExactly("맛있는 사과", "가나다라마사과과", "사과는 맛있어", "사과");
    }

    @Test
    @DisplayName("빈 문자열의 키워드로 검색하면 에러를 반환한다")
    void searchProducts_emptyKeyword() {
        // given
        insertProduct("사과", 2500);    // O
        insertProduct("맛있는 사과", 3000);    // O
        insertProduct("사과는 맛있어", 1000);    // O
        insertProduct("가나다라마사과과", 4000);    // O
        insertProduct("가나다라마바사", 2000);    // X
        insertProduct("과놔돠롸", 4500);    // X

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", "")
                .queryParam("sort", "createdAt,desc")
                .when().get("/api/v1/products/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMPTY_INPUT);
    }

    @Test
    @DisplayName("키워드 없이 검색하면 에러를 반환한다")
    void searchProducts_noKeyword() {
        // given
        insertProduct("사과", 2500);    // O
        insertProduct("맛있는 사과", 3000);    // O
        insertProduct("사과는 맛있어", 1000);    // O
        insertProduct("가나다라마사과과", 4000);    // O
        insertProduct("가나다라마바사", 2000);    // X
        insertProduct("과놔돠롸", 4500);    // X

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/api/v1/products/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMPTY_INPUT);
    }

    @Test
    @DisplayName("이상한 정렬 조건으로 검색하면 에러를 반환한다")
    void searchProducts_invalidSort() {
        // given
        insertProduct("사과", 2500);    // O
        insertProduct("맛있는 사과", 3000);    // O
        insertProduct("사과는 맛있어", 1000);    // O
        insertProduct("가나다라마사과과", 4000);    // O
        insertProduct("가나다라마바사", 2000);    // X
        insertProduct("과놔돠롸", 4500);    // X

        final String keyword = "사과";

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .queryParam("sort", "invalidSort,desc")
                .when().get("/api/v1/products/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_SORT);
    }

    @Test
    @DisplayName("상품 상세 페이지를 반환한다")
    void productDetail() {
        // given
        final String name = "맛있는 사과";
        final long price = 3000;
        final Long productId = insertProduct(name, price);

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/api/v1/products/{productId}", productId)
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final ProductDetailResponse result = response.as(ProductDetailResponse.class);
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo("test_description");
        assertThat(result.getImageFileName()).isEqualTo("image_file_" + name);
        assertThat(result.getPrice()).isEqualTo(price);
    }

    @Test
    @DisplayName("상품 상세 페이지 조회 시, id 와 일치하는 상품이 없으면 404 를 반환한다")
    void productDetail_notExistProductId_status404() {
        // given
        final Long notExistProductId = 100_000L;

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/api/v1/products/{productId}", notExistProductId)
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        final ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PRODUCT);
        assertThat(errorResponse.getMessage()).isEqualTo(ErrorCode.INVALID_PRODUCT.getMessage());
    }

    private Long insertProduct(final String productName, final long price) {
        final Product product = DomainUtils.createProductWithoutId(productName, price, 10);
        return productRepository.save(product).getId();
    }

    private void insertAllProducts(final List<String> productNames) {
        productNames.forEach(name -> insertProduct(name, 1000L));
    }
}
