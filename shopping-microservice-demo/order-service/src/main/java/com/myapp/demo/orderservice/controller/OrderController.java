package com.myapp.demo.orderservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.myapp.demo.orderservice.dto.OrderRequest;
import com.myapp.demo.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallback")
//    @TimeLimiter(name = "inventory")
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        // return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));
        return orderService.placeOrder(orderRequest);
    }

//    public CompletableFuture<String> placeOrderFallback(OrderRequest orderRequest, RuntimeException runtimeException) {
//        return CompletableFuture.supplyAsync(() -> "Oops, Something went wrong");
//    }
}
