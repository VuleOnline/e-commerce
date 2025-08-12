package com.vule.cart_service.dto;

import lombok.*;

@Data
public class CartItemDto {
    private Long productId;
    private int quantity;
}