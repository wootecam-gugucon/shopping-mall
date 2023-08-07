package shopping.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("상품 기능 통합 테스트")
class ProductIntegrationTest extends IntegrationTest {

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("성공 : 상품 전체 목록을 조회한다.")
    void findAllProducts() {
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
