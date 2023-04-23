package com.myapp.demo.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.myapp.demo.orderservice.dto.InventoryResponse;
import com.myapp.demo.orderservice.dto.OrderLineItemsDto;
import com.myapp.demo.orderservice.event.OrderPlaceEvent;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
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
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    private final Tracer tracer;

    private final KafkaTemplate<String, OrderPlaceEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemDtos().stream()
            .map(OrderUtil::mapToOrderLineItem).toList();

        order.setOrderLineItems(orderLineItems);

        List<String> skuCodes = orderRequest.getOrderLineItemDtos().stream()
                .map(OrderLineItemsDto::getSkuCode).toList();

        log.info("Calling the inventory service");

        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

        try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {
            // call to inventory service for stock check upon sku code
            InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductInStock = Arrays.stream(inventoryResponses)
                    .allMatch(InventoryResponse::isHasStock);

            if(allProductInStock) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlaceEvent(order.getOrderNumber()));
                return "Order has been placed successfully";
            }
            else
                throw new IllegalArgumentException("Product is not in stock, please try again later.");
        } finally {
            inventoryServiceLookup.end();
        }
    }
}
