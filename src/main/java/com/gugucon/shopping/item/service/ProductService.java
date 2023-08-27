package com.gugucon.shopping.item.service;

import com.gugucon.shopping.common.dto.response.PagedResponse;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.SearchCondition;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.response.ProductDetailResponse;
import com.gugucon.shopping.item.dto.response.ProductRecommendResponse;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public PagedResponse<ProductResponse> readAllProducts(final Pageable pageable) {
        final Page<Product> products = productRepository.findAll(pageable);
        return convertToPage(products);
    }

    public PagedResponse<ProductResponse> searchProducts(final SearchCondition searchCondition) {
        searchCondition.validateSort();
        searchCondition.validateKeywordNotBlank();

        return convertToPage(searchProductsByCondition(searchCondition));
    }

    private Page<Product> searchProductsByCondition(final SearchCondition searchCondition) {
        if (searchCondition.isSortedByRate()) {
            return searchProductsSortByRate(searchCondition);
        }
        if (searchCondition.isSortedByOrderCount()) {
            return searchProductsSortByOrderCount(searchCondition);
        }
        return searchProductsSortBy(searchCondition);
    }

    private Page<Product> searchProductsSortBy(final SearchCondition searchCondition) {
        return productRepository.findAllByNameContainingIgnoreCase(searchCondition.getKeyword(),
                                                                   searchCondition.getPageable());
    }

    private Page<Product> searchProductsSortByOrderCount(final SearchCondition searchCondition) {
        final Pageable newPageable = createPageable(searchCondition.getPageable());
        if (searchCondition.hasValidFilters()) {
            return productRepository.findAllByNameFilterWithBirthYearRangeAndGenderSortByOrderCountDesc(
                    searchCondition.getKeyword(),
                    searchCondition.getBirthYearRange(),
                    searchCondition.getGender(),
                    newPageable
            );
        }
        return productRepository.findAllByNameSortByOrderCountDesc(searchCondition.getKeyword(), newPageable);
    }

    private Page<Product> searchProductsSortByRate(final SearchCondition searchCondition) {
        final Pageable newPageable = createPageable(searchCondition.getPageable());
        if (searchCondition.hasValidFilters()) {
            return productRepository.findAllByNameFilterWithBirthYearRangeAndGenderSortByRateDesc(
                    searchCondition.getKeyword(),
                    searchCondition.getBirthYearRange(),
                    searchCondition.getGender(),
                    newPageable
            );
        }
        return productRepository.findAllByNameSortByRateDesc(searchCondition.getKeyword(), newPageable);
    }

    private Pageable createPageable(final Pageable pageable) {
        return Pageable.ofSize(pageable.getPageSize())
                .withPage(pageable.getPageNumber());
    }

    private PagedResponse<ProductResponse> convertToPage(final Page<Product> products) {
        final List<ProductResponse> contents = products.map(ProductResponse::from).toList();
        return new PagedResponse<>(contents, products.getTotalPages(), products.getNumber(), products.getSize());
    }

    public ProductDetailResponse getProductDetail(final Long productId) {
        final Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_PRODUCT));
        return ProductDetailResponse.from(product);
    }

    public ProductRecommendResponse getRecommendations(final Long productId) {
        validateProductExistence(productId);

        final List<Product> recommendations = productRepository.findRecommendedProducts(productId);
        return ProductRecommendResponse.of(recommendations);
    }

    private void validateProductExistence(final Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ShoppingException(ErrorCode.INVALID_PRODUCT);
        }
    }
}
