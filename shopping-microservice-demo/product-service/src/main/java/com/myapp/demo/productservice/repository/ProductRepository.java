package com.myapp.demo.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.myapp.demo.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    
}
