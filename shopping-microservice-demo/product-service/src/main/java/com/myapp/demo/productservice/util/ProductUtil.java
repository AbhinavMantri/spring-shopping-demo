package com.myapp.demo.productservice.util;

import com.myapp.demo.productservice.dto.ProductResponse;
import com.myapp.demo.productservice.model.Product;

public class ProductUtil {
    public static ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .build();
    }
}
