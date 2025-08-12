package com.vule.order_service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.vule.order_service.dto.CartItemDto;

@FeignClient(name = "cart-service")
public interface CartClient {
	
	@GetMapping("/cart")
	ResponseEntity<List<CartItemDto>> getCart(@RequestHeader("Authorization") String authHeader);

}
