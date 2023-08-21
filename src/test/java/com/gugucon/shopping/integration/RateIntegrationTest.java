package com.gugucon.shopping.integration;

import static com.gugucon.shopping.utils.ApiUtils.getFirstOrderItem;
import static com.gugucon.shopping.utils.ApiUtils.loginAfterSignUp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.gugucon.shopping.integration.config.IntegrationTest;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.request.RateCreateRequest;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.order.repository.OrderRepository;
import com.gugucon.shopping.utils.ApiUtils;
import com.gugucon.shopping.utils.DomainUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@IntegrationTest
@DisplayName("별점 기능 통합 테스트")
class RateIntegrationTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    @DisplayName("주문 상품에 별점을 남긴다")
    void rate() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final Long orderId = buyProductWithSuccess(accessToken, "good product");
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();
        final short score = 3;

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .body(new RateCreateRequest(orderItemId, score))
            .contentType(ContentType.JSON)
            .when()
            .post("/api/v1/rate")
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("유효하지 않은 주문 상품에 별점을 남기면 404 상태를 반환한다")
    void rate_notInvalidOrderItem_status404() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final long invalidOrderItemId = 1_000_000L;
        final short score = 3;

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .body(new RateCreateRequest(invalidOrderItemId, score))
            .contentType(ContentType.JSON)
            .when()
            .post("/api/v1/rate")
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("별점이 1-5 사이의 정수가 아니면 400 상태를 반환한다")
    void rate_notInvalidScore_status400() {
        // given
        final String accessToken = loginAfterSignUp("test_email@woowafriends.com", "test_password!");
        final Long orderId = buyProductWithSuccess(accessToken, "good product");
        final long orderItemId = getFirstOrderItem(accessToken, orderId).getId();
        final short score = 6;

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .body(new RateCreateRequest(orderItemId, score))
            .contentType(ContentType.JSON)
            .when()
            .post("/api/v1/rate")
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Long insertProduct(final String productName, final long price) {
        final Product product = DomainUtils.createProductWithoutId(productName, price, 10);
        productRepository.save(product);
        return product.getId();
    }

    private Long buyProductWithSuccess(String accessToken, String productName) {
        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(ExpectedCount.twice(), anything())
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{ \"status\": \"DONE\" }", MediaType.APPLICATION_JSON));
        return ApiUtils.buyProduct(accessToken, insertProduct(productName, 1000L), 10);
    }
}
