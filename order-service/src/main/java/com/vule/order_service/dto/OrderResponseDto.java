package com.vule.order_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderResponseDto {
    private Long id;
    private String username;
    private List<OrderItemDto> items;
    private DeliveryInfoDto deliveryInfo;
    private double totalPrice;
    private String status;
}