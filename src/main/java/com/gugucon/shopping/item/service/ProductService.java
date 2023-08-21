package com.gugucon.shopping.item.service;

import com.gugucon.shopping.common.dto.response.PagedResponse;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.item.domain.entity.Product;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private static final Sort SORT_BY_ORDER_COUNT = Sort.by(Sort.Direction.DESC, "orderCount");

    private final ProductRepository productRepository;

    public PagedResponse<ProductResponse> readAllProducts(final Pageable pageable) {
        final Page<Product> products = productRepository.findAll(pageable);
        return convertToPage(products);
    }

    public PagedResponse<ProductResponse> searchProducts(final String keyword, final Pageable pageable) {
        validateNotBlack(keyword);
        if (pageable.getSort().equals(SORT_BY_ORDER_COUNT)) {
            return searchProductsSortByOrderCount(keyword, pageable);
        }
        return searchProductsSortBy(keyword, pageable);
    }

    private PagedResponse<ProductResponse> searchProductsSortBy(final String keyword, final Pageable pageable) {
        try {
            final Page<Product> products = productRepository.findAllByNameContainingIgnoreCase(keyword, pageable);
            return convertToPage(products);
        } catch (final PropertyReferenceException exception) {
            throw new ShoppingException(ErrorCode.INVALID_SORT_KEY);
        }
    }

    private void validateNotBlack(final String keyword) {
        if (keyword.isBlank()) {
            throw new ShoppingException(ErrorCode.EMPTY_INPUT);
        }
    }

    private PagedResponse<ProductResponse> searchProductsSortByOrderCount(final String keyword,
                                                                          final Pageable pageable) {
        final Pageable newPageable = createPageable(pageable);
        final Page<Product> products = productRepository.findAllByNameSortByOrderCountDesc(keyword, newPageable);
        return convertToPage(products);
    }

    private Pageable createPageable(final Pageable pageable) {
        final Pageable newPageable = Pageable.ofSize(pageable.getPageSize());
        newPageable.withPage(pageable.getPageNumber());
        return newPageable;
    }

    private PagedResponse<ProductResponse> convertToPage(final Page<Product> products) {
        final List<ProductResponse> contents = products.map(ProductResponse::from).toList();
        return new PagedResponse<>(contents, products.getTotalPages(), products.getNumber(), products.getSize());
    }
}
