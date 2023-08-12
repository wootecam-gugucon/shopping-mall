package com.gugucon.shopping.item.service;

import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> findAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }
}
