package com.myapp.demo.orderservice.util;

import com.myapp.demo.orderservice.dto.OrderLineItemsDto;
import com.myapp.demo.orderservice.model.OrderLineItems;

public class OrderUtil {
    public static OrderLineItems mapToOrderLineItem(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }
}
