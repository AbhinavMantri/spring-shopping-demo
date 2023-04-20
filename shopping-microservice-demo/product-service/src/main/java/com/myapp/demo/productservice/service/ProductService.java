package com.myapp.demo.productservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myapp.demo.productservice.dto.ProductRequest;
import com.myapp.demo.productservice.dto.ProductResponse;
import com.myapp.demo.productservice.model.Product;
import com.myapp.demo.productservice.repository.ProductRepository;
import com.myapp.demo.productservice.util.ProductUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository repository;

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
        .name(productRequest.getName())
        .description(productRequest.getDescription())
        .price(productRequest.getPrice()).build();

        repository.save(product);

        log.info("Product {} has been created", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = repository.findAll();
        
        return products.stream().map(ProductUtil::mapToProductResponse).toList();
    }
}
