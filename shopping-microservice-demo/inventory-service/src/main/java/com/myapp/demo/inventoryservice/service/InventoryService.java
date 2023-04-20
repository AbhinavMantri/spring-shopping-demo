package com.myapp.demo.inventoryservice.service;

import com.myapp.demo.inventoryservice.dto.InventoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myapp.demo.inventoryservice.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
       return inventoryRepository.findBySkuCodeIn(skuCode).stream()
               .map(inventory -> {
                   return InventoryResponse.builder()
                           .skuCode(inventory.getSkuCode())
                           .hasStock(inventory.getQuantity() >= 1)
                           .build();
               }).toList();
    }
}
