package com.vule.order_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {
	
	 	@NotNull
	    private String firstName;
	    @NotNull
	    private String lastName;
	    @NotNull
	    private String username;
	    @NotNull
	    private String email;
	    @NotNull
	    private String phone;
	    @NotNull
	    private String password;

	    @NotNull
	    private AddressDto address;
}
