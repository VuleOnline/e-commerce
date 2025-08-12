package com.vule.order_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class ProductDto {
	
		private Long id;
	
	    @NotBlank(message = "Name is required")
	    private String name;
	    

	    @NotBlank(message = "Category is required")
	    private String category;

	    @NotNull(message = "Price is required")
	    @Positive(message = "Price must be positive")
	    private Double price;

	    private Integer stock;
	}


