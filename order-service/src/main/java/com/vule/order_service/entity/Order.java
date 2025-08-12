package com.vule.order_service.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.vule.order_service.dto.AddressDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // može biti null ako nije ulogovan
    private String deliveryName;
    private String deliveryLastName;
    private AddressDto deliveryAddress;
    private String deliveryPhone;

    private Double totalPrice;
    private String status; // CREATED, SHIPPED itd.

    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
   
}
