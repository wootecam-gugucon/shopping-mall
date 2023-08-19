package com.gugucon.shopping.item.service;

import com.gugucon.shopping.common.dto.response.PagedResponse;
import com.gugucon.shopping.item.domain.entity.Product;
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

    public PagedResponse<ProductResponse> searchProducts(final String keyword, final Pageable pageable) {
        final Page<Product> products = productRepository.findAllByNameContainingIgnoreCase(keyword, pageable);
        return convertToPage(products);
    }

    private static PagedResponse<ProductResponse> convertToPage(final Page<Product> products) {
        final List<ProductResponse> contents = products.map(ProductResponse::from).toList();
        return new PagedResponse<>(contents, products.getTotalPages(), products.getNumber(), products.getSize());
    }
}
