package com.gugucon.shopping.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.gugucon.shopping.common.dto.response.PagedResponse;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.repository.ProductRepository;
import com.gugucon.shopping.utils.DomainUtils;
import java.util.List;
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

        final List<Product> products = List.of(
            DomainUtils.createProduct("치킨", 20000),
            DomainUtils.createProduct("피자", 20000),
            DomainUtils.createProduct("사케", 30000)
        );
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(products));

        /* when */
        final PagedResponse<ProductResponse> productResponses = productService.readAllProducts(pageable);

        /* then */
        final List<String> names = productResponses.getContents()
            .stream()
            .map(ProductResponse::getName)
            .toList();

        assertThat(names).containsExactly("치킨", "피자", "사케");
        assertThat(productResponses.getContents()).hasSize(3);
        assertThat(productResponses.getCurrentPage()).isZero();
        assertThat(productResponses.getSize()).isEqualTo(3);
        assertThat(productResponses.getTotalPage()).isEqualTo(1);
    }
}
