package com.gugucon.shopping.item.controller.api;

import com.gugucon.shopping.common.dto.response.PagedResponse;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public PagedResponse<ProductResponse> getProducts(@SortDefault(sort = "createdAt", direction = Direction.DESC)
                                                          final Pageable pageable) {
        return productService.readAllProducts(pageable);
    }
}
