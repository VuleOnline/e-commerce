package com.vule.order_service.dto;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Embeddable
public class AddressDto {
	
	 	@NotNull
	    private String street;
	    @NotNull
	    private String city;
	    @NotNull
	    private Long postalCode;
	    @NotNull
	    private String country;
	    
	    public String format() {
	        return street + ", " + city + ", " + postalCode + ", " + country;
	    }

}
