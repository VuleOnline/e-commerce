package com.vule.cart_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CartRequestDto {

	 	@NotNull
	    @Min(1)
	    private Long productId;
	    @NotNull
	    @Min(1)
	    private int quantity;
}
