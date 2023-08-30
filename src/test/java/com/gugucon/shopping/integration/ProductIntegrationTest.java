package com.gugucon.shopping.integration;

import static com.gugucon.shopping.member.domain.vo.BirthYearRange.EARLY_TWENTIES;
import static com.gugucon.shopping.member.domain.vo.BirthYearRange.LATE_TWENTIES;
import static com.gugucon.shopping.member.domain.vo.BirthYearRange.MID_TWENTIES;
import static com.gugucon.shopping.member.domain.vo.BirthYearRange.OVER_FORTIES;
import static com.gugucon.shopping.member.domain.vo.BirthYearRange.THIRTIES;
import static com.gugucon.shopping.member.domain.vo.BirthYearRange.UNDER_TEENS;
import static com.gugucon.shopping.utils.ApiUtils.buyAllProductsByPoint;
import static com.gugucon.shopping.utils.ApiUtils.chargePoint;
import static com.gugucon.shopping.utils.ApiUtils.createRateToOrderedItem;
import static com.gugucon.shopping.utils.ApiUtils.insertCartItem;
import static com.gugucon.shopping.utils.ApiUtils.loginAfterSignUp;
import static com.gugucon.shopping.utils.ApiUtils.payOrderByPoint;
import static com.gugucon.shopping.utils.ApiUtils.placeOrder;
import static com.gugucon.shopping.utils.ApiUtils.putOrder;
import static com.gugucon.shopping.utils.ApiUtils.readCartItems;
import static com.gugucon.shopping.utils.ApiUtils.updateCartItem;
import static com.gugucon.shopping.utils.StatsUtils.createInitialOrderStat;
import static com.gugucon.shopping.utils.StatsUtils.createInitialRateStat;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ErrorResponse;
import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.CartItemInsertRequest;
import com.gugucon.shopping.item.dto.request.CartItemUpdateRequest;
import com.gugucon.shopping.item.dto.response.CartItemResponse;
import com.gugucon.shopping.item.dto.response.ProductDetailResponse;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.repository.OrderStatRepository;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.item.repository.RateStatRepository;
import com.gugucon.shopping.member.domain.vo.BirthYearRange;
import com.gugucon.shopping.member.domain.vo.Gender;
import com.gugucon.shopping.order.domain.PayType;
import com.gugucon.shopping.order.dto.request.OrderPayRequest;
import com.gugucon.shopping.order.dto.response.OrderDetailResponse;
import com.gugucon.shopping.order.dto.response.OrderItemResponse;
import com.gugucon.shopping.order.dto.response.OrderPayResponse;
import com.gugucon.shopping.pay.dto.request.PointPayRequest;
import com.gugucon.shopping.rate.dto.request.RateCreateRequest;
import com.gugucon.shopping.utils.DomainUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@IntegrationTest
@DisplayName("상품 기능 통합 테스트")
class ProductIntegrationTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderStatRepository orderStatRepository;
    @Autowired
    private RateStatRepository rateStatRepository;

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
                .queryParam("sort", "id,desc")
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
        final List<Long> productIds = List.of(사과_id, 맛있는사과_id, 사과는맛있어_id, 가나다라마사과과_id);

        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        insertCartItem(accessToken, new CartItemInsertRequest(사과_id));
        insertCartItem(accessToken, new CartItemInsertRequest(사과는맛있어_id));
        insertCartItem(accessToken, new CartItemInsertRequest(가나다라마사과과_id));
        insertCartItem(accessToken, new CartItemInsertRequest(맛있는사과_id));

        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
        final Long 사과_장바구니_id = cartItemResponses.get(0).getCartItemId();
        final Long 사과는맛있어_장바구니_id = cartItemResponses.get(1).getCartItemId();
        final Long 가나다라마사과과_장바구니_id = cartItemResponses.get(2).getCartItemId();
        final Long 맛있는사과_장바구니_id = cartItemResponses.get(3).getCartItemId();

        updateCartItem(accessToken, 사과_장바구니_id, new CartItemUpdateRequest(10));
        updateCartItem(accessToken, 사과는맛있어_장바구니_id, new CartItemUpdateRequest(9));
        updateCartItem(accessToken, 가나다라마사과과_장바구니_id, new CartItemUpdateRequest(8));
        updateCartItem(accessToken, 맛있는사과_장바구니_id, new CartItemUpdateRequest(7));

        initializeAllOrderStats(productIds);
        buyAllProductsByPoint(accessToken, productIds, 1000000L);

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

        initializeAllRatesStats(productIds);
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
    @DisplayName("검색어로 검색한 결과를 나이대, 성별로 필터링한 후 주문이 많은 순으로 정렬하여 반환한다")
    void searchProducts_filterWithBirthYearRangeAndGenderSortByOrderCountDesc() {
        // given
        final Long 사과_id = insertProduct("사과", 2500);// O
        final Long 맛있는사과_id = insertProduct("맛있는 사과", 3000);    // O
        final Long 사과는맛있어_id = insertProduct("사과는 맛있어", 1000);    // O
        final Long 가나다라마사과과_id = insertProduct("가나다라마사과과", 4000);    // O
        final Long 가나다라마바사_id = insertProduct("가나다라마바사", 2000);    // X
        final Long 과놔돠롸_id = insertProduct("과놔돠롸", 4500);    // X
        final List<Long> productIds = List.of(사과_id, 맛있는사과_id, 사과는맛있어_id, 가나다라마사과과_id);

        final String keyword = "사과";
        final BirthYearRange birthYearRange = BirthYearRange.MID_TWENTIES;
        final Gender gender = Gender.MALE;

        createOrderStat(사과_id, birthYearRange, gender);
        createOrderStat(맛있는사과_id, birthYearRange, gender);
        createOrderStat(사과는맛있어_id, birthYearRange, gender);
        createOrderStat(가나다라마사과과_id, birthYearRange, gender);

        final String accessToken = loginAfterSignUp(birthYearRange, gender);

        insertCartItem(accessToken, new CartItemInsertRequest(사과_id));
        insertCartItem(accessToken, new CartItemInsertRequest(사과는맛있어_id));
        insertCartItem(accessToken, new CartItemInsertRequest(가나다라마사과과_id));
        insertCartItem(accessToken, new CartItemInsertRequest(맛있는사과_id));

        final List<CartItemResponse> cartItemResponses = readCartItems(accessToken);
        final Long 사과_장바구니_id = cartItemResponses.get(0).getCartItemId();
        final Long 사과는맛있어_장바구니_id = cartItemResponses.get(1).getCartItemId();
        final Long 가나다라마사과과_장바구니_id = cartItemResponses.get(2).getCartItemId();
        final Long 맛있는사과_장바구니_id = cartItemResponses.get(3).getCartItemId();

        updateCartItem(accessToken, 사과_장바구니_id, new CartItemUpdateRequest(10));
        updateCartItem(accessToken, 사과는맛있어_장바구니_id, new CartItemUpdateRequest(9));
        updateCartItem(accessToken, 가나다라마사과과_장바구니_id, new CartItemUpdateRequest(8));
        updateCartItem(accessToken, 맛있는사과_장바구니_id, new CartItemUpdateRequest(7));

        final Long orderId = placeOrder(accessToken);
        chargePoint(accessToken, 1_000_000L);
        final OrderPayResponse orderPayResponse = putOrder(accessToken,
                                                           new OrderPayRequest(orderId, PayType.POINT));
        payOrderByPoint(accessToken, new PointPayRequest(orderPayResponse.getOrderId()));

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .queryParam("sort", "orderCount,desc")
                .queryParam("birthYearRange", birthYearRange.name())
                .queryParam("gender", gender.name())
                .when().get("/api/v1/products/search")
                .then().contentType(ContentType.JSON).log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<String> names = response.body()
                .jsonPath()
                .getList("contents", ProductResponse.class)
                .stream().map(ProductResponse::getName).toList();

        assertThat(names).containsExactly("사과", "사과는 맛있어", "가나다라마사과과", "맛있는 사과");
    }

    @Test
    @DisplayName("검색어로 검색한 결과를 나이대, 성별로 필터링한 후 별점이 높은 순으로 정렬하여 반환한다")
    void searchProducts_filterWithBirthYearRangeAndGenderSortByRateDesc() {
        // given
        final Long 사과_id = insertProduct("사과", 2500);// O
        final Long 맛있는사과_id = insertProduct("맛있는 사과", 3000);    // O
        final Long 사과는맛있어_id = insertProduct("사과는 맛있어", 1000);    // O
        final Long 가나다라마사과과_id = insertProduct("가나다라마사과과", 4000);    // O
        final Long 가나다라마바사_id = insertProduct("가나다라마바사", 2000);    // X
        final Long 과놔돠롸_id = insertProduct("과놔돠롸", 4500);    // X
        final List<Long> productIds = List.of(사과_id, 맛있는사과_id, 사과는맛있어_id, 가나다라마사과과_id);

        final String keyword = "사과";
        final BirthYearRange birthYearRange = BirthYearRange.MID_TWENTIES;
        final Gender gender = Gender.MALE;

        createRateStat(사과_id, birthYearRange, gender);
        createRateStat(맛있는사과_id, birthYearRange, gender);
        createRateStat(사과는맛있어_id, birthYearRange, gender);
        createRateStat(가나다라마사과과_id, birthYearRange, gender);

        final String accessToken = loginAfterSignUp(birthYearRange, gender);

        final OrderDetailResponse orderDetailResponse = buyAllProductsByPoint(accessToken, productIds, 10500L);
        final List<OrderItemResponse> orderItemResponses = orderDetailResponse.getOrderItems();
        final Long 사과_주문_id = orderItemResponses.get(0).getId();
        final Long 맛있는사과_주문_id = orderItemResponses.get(1).getId();
        final Long 사과는맛있어_주문_id = orderItemResponses.get(2).getId();
        final Long 가나다라마사과과_주문_id = orderItemResponses.get(3).getId();

        createRateToOrderedItem(accessToken, new RateCreateRequest(사과_주문_id, (short) 1));
        createRateToOrderedItem(accessToken, new RateCreateRequest(사과는맛있어_주문_id, (short) 2));
        createRateToOrderedItem(accessToken, new RateCreateRequest(가나다라마사과과_주문_id, (short) 3));
        createRateToOrderedItem(accessToken, new RateCreateRequest(맛있는사과_주문_id, (short) 4));

        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("keyword", keyword)
                .queryParam("sort", "rate,desc")
                .queryParam("birthYearRange", birthYearRange.name())
                .queryParam("gender", gender.name())
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
                .queryParam("sort", "id,desc")
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

    @Test
    @DisplayName("해당 상품을 구매한 사용자들이 함께 찾는 상품 목록을 기본 페이징을 적용하여 반환한다. (page=0, size=5)")
    void recommendedProducts_defaultPaging() {
        // given
        final long 아디다스_크롭_탑 = insertProduct("아디다스 크롭 탑", 49000); // 조회 상품
        final long 데비웨어_요가웨어 = insertProduct("데비웨어_요가웨어", 19780); // 연관 구매 3건
        final long 안다르_바이크_5부 = insertProduct("안다르 바이크 5부", 33000); // 연관 구매 2건
        final long 에이치덱스_땀복 = insertProduct("에이치덱스_땀복", 74400); // 연관 구매 2건
        final long 젝시믹스_머슬핏 = insertProduct("젝시믹스_머슬핏", 29000); // 연관 구매 1건

        buyAllProducts(List.of(아디다스_크롭_탑, 데비웨어_요가웨어, 안다르_바이크_5부));
        buyAllProducts(List.of(아디다스_크롭_탑, 데비웨어_요가웨어, 젝시믹스_머슬핏));
        buyAllProducts(List.of(아디다스_크롭_탑, 데비웨어_요가웨어, 에이치덱스_땀복));
        buyAllProducts(List.of(아디다스_크롭_탑, 안다르_바이크_5부, 에이치덱스_땀복));

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/api/v1/products/{productId}/recommend", 아디다스_크롭_탑)
            .then().contentType(ContentType.JSON).log().all()
            .extract();

        // then
        final List<Long> actualProductIds = response.body()
            .jsonPath()
            .getList("contents", ProductDetailResponse.class)
            .stream().map(ProductDetailResponse::getId).toList();
        assertThat(actualProductIds).containsExactly(데비웨어_요가웨어, 에이치덱스_땀복, 안다르_바이크_5부, 젝시믹스_머슬핏);
    }

    @Test
    @DisplayName("해당 상품을 구매한 사용자들이 함께 찾는 상품 목록을 페이징하여 반환한다 (page=1, size=2)")
    void recommendedProducts_paging() {
        // given
        final long 아디다스_크롭_탑 = insertProduct("아디다스 크롭 탑", 49000); // 조회 상품
        final long 데비웨어_요가웨어 = insertProduct("데비웨어_요가웨어", 19780); // 연관 구매 3건
        final long 안다르_바이크_5부 = insertProduct("안다르 바이크 5부", 33000); // 연관 구매 2건
        final long 에이치덱스_땀복 = insertProduct("에이치덱스_땀복", 74400); // 연관 구매 2건
        final long 젝시믹스_머슬핏 = insertProduct("젝시믹스_머슬핏", 29000); // 연관 구매 1건

        buyAllProducts(List.of(아디다스_크롭_탑, 데비웨어_요가웨어, 안다르_바이크_5부));
        buyAllProducts(List.of(아디다스_크롭_탑, 데비웨어_요가웨어, 젝시믹스_머슬핏));
        buyAllProducts(List.of(아디다스_크롭_탑, 데비웨어_요가웨어, 에이치덱스_땀복));
        buyAllProducts(List.of(아디다스_크롭_탑, 안다르_바이크_5부, 에이치덱스_땀복));

        // when
        final int currentPage = 1;
        final int size = 2;

        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/api/v1/products/{productId}/recommend?page=" + currentPage + "&size=" + size, 아디다스_크롭_탑)
            .then().contentType(ContentType.JSON).log().all()
            .extract();

        // then
        assertThat(response.body().jsonPath().getBoolean("hasNextPage")).isFalse();
        assertThat(response.body().jsonPath().getInt("currentPage")).isEqualTo(currentPage);
        assertThat(response.body().jsonPath().getInt("size")).isEqualTo(size);

        final List<Long> actualProductIds = response.body()
            .jsonPath()
            .getList("contents", ProductDetailResponse.class)
            .stream().map(ProductDetailResponse::getId).toList();
        assertThat(actualProductIds).containsExactly(안다르_바이크_5부, 젝시믹스_머슬핏);
    }

    private Long insertProduct(final String productName, final long price) {
        final Product product = DomainUtils.createProductWithoutId(productName, price, 10);
        return productRepository.save(product).getId();
    }

    private void insertAllProducts(final List<String> productNames) {
        productNames.forEach(name -> insertProduct(name, 1000L));
    }

    private void createOrderStat(final Long productId,
                                 final BirthYearRange birthYearRange,
                                 final Gender gender) {
        orderStatRepository.save(createInitialOrderStat(gender, birthYearRange, productId));
    }

    private void createRateStat(final Long productId,
                                final BirthYearRange birthYearRange,
                                final Gender gender) {
        rateStatRepository.save(createInitialRateStat(gender, birthYearRange, productId));
    }

    private void initializeAllRatesStats(final List<Long> productIds) {
        productIds.forEach(this::initializeAllAgeAndGenderProductStats);
    }

    private void initializeAllAgeAndGenderProductStats(final long productId) {
        createRateStat(productId, UNDER_TEENS, Gender.FEMALE);
        createRateStat(productId, UNDER_TEENS, Gender.MALE);
        createRateStat(productId, EARLY_TWENTIES, Gender.FEMALE);
        createRateStat(productId, EARLY_TWENTIES, Gender.MALE);
        createRateStat(productId, MID_TWENTIES, Gender.FEMALE);
        createRateStat(productId, MID_TWENTIES, Gender.MALE);
        createRateStat(productId, LATE_TWENTIES, Gender.FEMALE);
        createRateStat(productId, LATE_TWENTIES, Gender.MALE);
        createRateStat(productId, THIRTIES, Gender.FEMALE);
        createRateStat(productId, THIRTIES, Gender.MALE);
        createRateStat(productId, OVER_FORTIES, Gender.FEMALE);
        createRateStat(productId, OVER_FORTIES, Gender.MALE);
    }

    private void createOrderStats(final Long productId,
                                  final BirthYearRange birthYearRange,
                                  final Gender gender) {
        orderStatRepository.save(createInitialOrderStat(gender, birthYearRange, productId));
    }

    private void initializeAllOrderStats(final List<Long> productIds) {
        productIds.forEach(this::initializeAllAgeAndGenderOrderStats);
    }

    private void initializeAllAgeAndGenderOrderStats(final long productId) {
        createOrderStats(productId, UNDER_TEENS, Gender.FEMALE);
        createOrderStats(productId, UNDER_TEENS, Gender.MALE);
        createOrderStats(productId, EARLY_TWENTIES, Gender.FEMALE);
        createOrderStats(productId, EARLY_TWENTIES, Gender.MALE);
        createOrderStats(productId, MID_TWENTIES, Gender.FEMALE);
        createOrderStats(productId, MID_TWENTIES, Gender.MALE);
        createOrderStats(productId, LATE_TWENTIES, Gender.FEMALE);
        createOrderStats(productId, LATE_TWENTIES, Gender.MALE);
        createOrderStats(productId, THIRTIES, Gender.FEMALE);
        createOrderStats(productId, THIRTIES, Gender.MALE);
        createOrderStats(productId, OVER_FORTIES, Gender.FEMALE);
        createOrderStats(productId, OVER_FORTIES, Gender.MALE);
    }

    private void buyAllProducts(List<Long> productIds) {
        final String accessToken = loginAfterSignUp("test@woowa.com", "1234abc");
        buyAllProductsByPoint(accessToken, productIds, 10000000L);
    }
}
