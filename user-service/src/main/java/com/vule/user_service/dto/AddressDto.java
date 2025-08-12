package com.vule.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressDto {
	
	 	@NotNull
	    private String street;
	    @NotNull
	    private String city;
	    @NotNull
	    private Long postalCode;
	    @NotNull
	    private String country;

}
