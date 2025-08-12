package com.vule.order_service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vule.order_service.dto.DeliveryInfoDto;
import com.vule.order_service.dto.OrderItemDto;
import com.vule.order_service.dto.OrderResponseDto;
import com.vule.order_service.entity.DeliveryInfo;
import com.vule.order_service.entity.Order;
import com.vule.order_service.entity.OrderItem;

@Component
public class OrderMapper {
    
    public OrderResponseDto toDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setUsername(order.getDeliveryName());
        dto.setItems(order.getItems().stream().map(this::toOrderItemDto).collect(Collectors.toList()));
        
        DeliveryInfoDto info = new DeliveryInfoDto();
        info.setFirstName(order.getDeliveryName());
        info.setLastName(order.getDeliveryLastName());
        info.setAddress(order.getDeliveryAddress());
        info.setPhone(order.getDeliveryPhone());
        dto.setDeliveryInfo(info);
        
        dto.setTotalPrice(calculateTotalPrice(order.getItems()));
        dto.setStatus(order.getStatus());
        return dto;
    }

    
    private OrderItemDto toOrderItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }

    
    private DeliveryInfoDto toDeliveryInfoDto(DeliveryInfo deliveryInfo) {
        DeliveryInfoDto dto = new DeliveryInfoDto();
        dto.setFirstName(deliveryInfo.getFirstName());
        dto.setLastName(deliveryInfo.getLastName());
        dto.setAddress(deliveryInfo.getAddress());
        dto.setPhone(deliveryInfo.getPhone());
        return dto;
    }

    
    private double calculateTotalPrice(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
