package com.gugucon.shopping.item.controller.api;

import com.gugucon.shopping.common.dto.response.PagedResponse;
import com.gugucon.shopping.item.dto.response.ProductDetailResponse;
import com.gugucon.shopping.item.dto.response.ProductResponse;
import com.gugucon.shopping.item.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PagedResponse<ProductResponse> getProducts(@SortDefault(sort = "createdAt", direction = Direction.DESC) final Pageable pageable) {
        return productService.readAllProducts(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public PagedResponse<ProductResponse> searchProducts(@RequestParam final String keyword,
                                                         @SortDefault.SortDefaults(value = {
                                                                 @SortDefault(sort = "createdAt", direction = Direction.DESC),
                                                                 @SortDefault(sort = "price")
                                                         }) final Pageable pageable) {
        return productService.searchProducts(keyword, pageable);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDetailResponse getProductDetail(@PathVariable final Long productId) {
        return productService.getProductDetail(productId);
    }
}
