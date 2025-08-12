package com.vule.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.vule.order_service.dto.ProductDto;



@FeignClient(name = "product-service")
public interface ProductClient {
	
	    @GetMapping("/product/{id}")
	    ResponseEntity<ProductDto> getProductById(@PathVariable Long id);
	    
	    @PatchMapping("product/{id}/stock")
	    public ResponseEntity<String> updateStock(@PathVariable Long id, @RequestBody int quantity);

}
