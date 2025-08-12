package com.vule.order_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequestDto {
	
	private Long userId; // može biti null ako nije ulogovan
    private DeliveryInfoDto deliveryInfo; // popunjeno ako nije ulogovan
    private List<CartItemDto> cartItems;

}
