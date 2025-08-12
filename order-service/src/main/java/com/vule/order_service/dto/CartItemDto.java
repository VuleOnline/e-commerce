package com.vule.order_service.dto;

import lombok.Data;

@Data
public class CartItemDto {
	
	private Long productId;
    private Integer quantity;

}
