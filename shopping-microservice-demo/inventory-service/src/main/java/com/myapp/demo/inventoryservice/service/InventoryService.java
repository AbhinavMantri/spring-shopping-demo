package com.myapp.demo.inventoryservice.service;

import com.myapp.demo.inventoryservice.dto.InventoryResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myapp.demo.inventoryservice.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCode) {
//       log.info("Wait Started");
//       Thread.sleep(10000);
//       log.info("Wait Ended");
       return inventoryRepository.findBySkuCodeIn(skuCode).stream()
               .map(inventory -> {
                   return InventoryResponse.builder()
                           .skuCode(inventory.getSkuCode())
                           .hasStock(inventory.getQuantity() >= 1)
                           .build();
               }).toList();
    }
}
