package com.myapp.demo.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.myapp.demo.orderservice.dto.InventoryResponse;
import com.myapp.demo.orderservice.dto.OrderLineItemsDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myapp.demo.orderservice.dto.OrderRequest;
import com.myapp.demo.orderservice.model.Order;
import com.myapp.demo.orderservice.model.OrderLineItems;
import com.myapp.demo.orderservice.repository.OrderRepository;
import com.myapp.demo.orderservice.util.OrderUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemDtos().stream()
            .map(OrderUtil::mapToOrderLineItem).toList();

        order.setOrderLineItems(orderLineItems);

        List<String> skuCodes = orderRequest.getOrderLineItemDtos().stream()
                .map(OrderLineItemsDto::getSkuCode).toList();

        // call to inventory service for stock check upon sku code
        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                        .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                                        .block();

        boolean allProductInStock = Arrays.stream(inventoryResponses)
                .allMatch(InventoryResponse::isHasStock);

        if(allProductInStock)
            orderRepository.save(order);
        else
            throw new IllegalArgumentException("Product is not in stock, please try again later.");

    }
}
