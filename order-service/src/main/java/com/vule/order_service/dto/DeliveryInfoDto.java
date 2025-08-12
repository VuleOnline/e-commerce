package com.vule.order_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeliveryInfoDto {
	
	@NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String phone;
    @NotNull
    private AddressDto address;

}
