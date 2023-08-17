package com.gugucon.shopping.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.gugucon.shopping.TestUtils;
import com.gugucon.shopping.common.dto.response.PagedResponse;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 단위 테스트")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("전체 상품 목록을 조회한다.")
    void readAllProducts() {
        /* given */
        final Pageable pageable = PageRequest.of(0, 20, Direction.DESC, "createdAt");

        List<Product> products = List.of(
            TestUtils.createProduct("치킨", 20000),
            TestUtils.createProduct("피자", 20000),
            TestUtils.createProduct("사케", 30000)
        );
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(products));

        /* when */
        PagedResponse<ProductResponse> productResponses = productService.readAllProducts(pageable);

        /* then */
        final List<String> names = productResponses.getData()
            .stream()
            .map(ProductResponse::getName)
            .collect(Collectors.toList());

        assertThat(names).containsExactly("치킨", "피자", "사케");
        assertThat(productResponses.getData()).hasSize(3);
        assertThat(productResponses.getTotalPage()).isEqualTo(1);
    }
}
