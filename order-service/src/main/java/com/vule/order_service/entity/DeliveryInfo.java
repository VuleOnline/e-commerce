package com.vule.order_service.entity;

import jakarta.persistence.Id;

import com.vule.order_service.dto.AddressDto;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class DeliveryInfo {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "Address is required")
    @Embedded
    private AddressDto address;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone")
    private String phone;

}
