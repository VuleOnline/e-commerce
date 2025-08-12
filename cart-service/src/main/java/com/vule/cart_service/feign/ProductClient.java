package com.vule.cart_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.vule.cart_service.dto.ProductDto;

@FeignClient(name = "product-service")
public interface ProductClient {
	
	@GetMapping("/product/{id}")
    ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id);
	
	@PatchMapping("/product/{id}/stock")
    ResponseEntity<String> updateStock(@PathVariable("id") Long id, @RequestBody int quantity);

    @PatchMapping("/product/{id}/stock/increase")
    ResponseEntity<String> increaseStock(@PathVariable("id") Long id, @RequestBody int quantity);

}
