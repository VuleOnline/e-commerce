package com.vule.order_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vule.order_service.dto.DeliveryInfoDto;
import com.vule.order_service.dto.OrderResponseDto;
import com.vule.order_service.service.OrderService;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestHeader(value = "Authorization", 
    required = false) String authHeader, @Valid @RequestBody DeliveryInfoDto deliveryInfoDto) {
    	try {
    	    String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
    	    if (token == null) {
    	        return ResponseEntity.status(401).body("{\"error\": \"Missing or invalid Authorization header\"}");
    	    }
        OrderResponseDto order = orderService.createOrder(token, deliveryInfoDto);
        return ResponseEntity.ok(order);
    	} catch (JwtException e) {
    	    return ResponseEntity.status(401).body("{\"error\": \"Invalid JWT token\"}");
    	} catch (IllegalArgumentException e) {
    	    return ResponseEntity.status(400).body("{\"error\": \"" + e.getMessage() + "\"}");
    	} catch (Exception e) {
    	    return ResponseEntity.status(500).body("{\"error\": \"Internal server error\"}");
    }
    }
    
}
