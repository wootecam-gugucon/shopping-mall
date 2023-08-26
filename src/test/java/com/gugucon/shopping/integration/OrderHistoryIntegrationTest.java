package com.gugucon.shopping.integration;

import static com.gugucon.shopping.utils.ApiUtils.buyAllProductsByPoint;
import static com.gugucon.shopping.utils.ApiUtils.loginAfterSignUp;
import static org.assertj.core.api.Assertions.assertThat;

import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.order.dto.response.OrderHistoryResponse;
import com.gugucon.shopping.utils.DomainUtils;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@IntegrationTest
@DisplayName("주문한 상품 기능 통합 테스트")
class OrderHistoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("기본 설정 (page=0, size=20) 에 따라 페이징하여 반환한다.")
    void getOrderHistory_defaultPaging() {
        // given
        final String accessToken = loginAfterSignUp("test@woowa.com", "abc123456");
        final long orderId = orderCartItems(accessToken, "치킨", "사케");

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().get("/api/v1/order-history")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final JsonPath result = response.body().jsonPath();
        assertThat(result.getInt("currentPage")).isZero();
        assertThat(result.getInt("size")).isEqualTo(20);
        assertThat(result.getInt("currentPage")).isZero();

        final List<OrderHistoryResponse> orders = result.getList("contents", OrderHistoryResponse.class);
        assertThat(orders.get(0).getOrderId()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("입력된 페이징 조건에 따라 페이징하여 반환한다.")
    void getOrderHistory_paging() {
        // given
        final String accessToken = loginAfterSignUp("test@woowa.com", "abc123456");

        final long firstOrderId = orderCartItems(accessToken, "치킨");
        orderCartItems(accessToken, "사케");

        // when
        int page = 1;
        int size = 1;
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().get("/api/v1/order-history?page=" + page + "&size=" + size)
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final JsonPath result = response.body().jsonPath();
        assertThat(result.getInt("currentPage")).isEqualTo(page);
        assertThat(result.getInt("size")).isEqualTo(size);
        assertThat(result.getInt("totalPage")).isEqualTo(2);

        final List<OrderHistoryResponse> orders = result.getList("contents", OrderHistoryResponse.class);
        assertThat(orders.get(0).getOrderId()).isEqualTo(firstOrderId);
    }

    @Test
    @DisplayName("정렬 조건이 기재되지 않은 경우 최신순으로 정렬하여 반환한다")
    void getOrderHistory_defaultSorting() {
        // given
        final String accessToken = loginAfterSignUp("test@woowa.com", "abc123456");

        final long firstOrderId = orderCartItems(accessToken, "치킨");
        final long secondOrderId = orderCartItems(accessToken, "사케");

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .when().get("/api/v1/order-history")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final JsonPath result = response.body().jsonPath();
        assertThat(result.getInt("currentPage")).isZero();
        assertThat(result.getInt("size")).isEqualTo(20);
        assertThat(result.getInt("totalPage")).isEqualTo(1);

        final List<Long> actualOrderIds = result.getList("contents", OrderHistoryResponse.class).stream()
            .map(OrderHistoryResponse::getOrderId)
            .toList();
        assertThat(actualOrderIds).containsExactly(secondOrderId, firstOrderId);
    }

    private Long insertProduct(final String productName, final long price) {
        final Product product = DomainUtils.createProductWithoutId(productName, price, 10);
        return productRepository.save(product).getId();
    }

    private long orderCartItems(final String accessToken, final String ...productNames) {
        List<Long> productIds = new ArrayList<>();
        for (String productName : productNames) {
            final Long productId = insertProduct(productName, 1000L);
            productIds.add(productId);
        }
        return buyAllProductsByPoint(accessToken, productIds, 1000000L).getOrderId();
    }
}
