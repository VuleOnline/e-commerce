package com.vule.order_service.entity;

import com.vule.order_service.dto.OrderResponseDto;

import lombok.Data;

@Data
public class OrderResponse {
    private String message;
    private OrderResponseDto order;

    public OrderResponse(String message, OrderResponseDto order) {
        this.message = message;
        this.order = order;
    }
}
