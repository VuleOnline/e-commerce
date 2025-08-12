package com.vule.product_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vule.product_service.dto.ProductDto;
import com.vule.product_service.service.JwtService;
import com.vule.product_service.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {
	
	private final ProductService productService;
	private final JwtService jwtService;

    public ProductController(ProductService productService, JwtService jwtService) {
        this.productService = productService;
        this.jwtService = jwtService;
        
    }
	
	@GetMapping
	public ResponseEntity<?> getAllProducts(@RequestParam(defaultValue = "id") String sortBy) {
        List<ProductDto> products = productService.getAllProducts(sortBy);
        if (products.isEmpty()) {
            return ResponseEntity.status(204).body("No products available.");
        }
        return ResponseEntity.ok(products);
    }
	
	@GetMapping("/{category}")
	public ResponseEntity<?> getProductsByCategory(@PathVariable String category)
	{
		List<ProductDto> products = productService.getProductsByCategory(category);
        if (products.isEmpty()) {
            return ResponseEntity.status(204).body("No products available for category: " + category);
        }
        return ResponseEntity.ok(products);
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getProductById(@PathVariable Long id) {
		Optional<ProductDto> productDto = productService.getProductById(id);
				if (productDto.isPresent()) {
		            return ResponseEntity.ok(productDto.get());
		        }
		        return ResponseEntity.status(404).body("Product with ID " + id + " not found.");
	}
	
	@PostMapping
	public ResponseEntity<?> addProduct(@RequestHeader(value = "Authorization", required = false) String authHeader, @Valid @RequestBody ProductDto productDto) {
	        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
	        if (token == null || !jwtService.hasAdminRole(token)) {
	            return ResponseEntity.status(403).body("Unauthorized: Admin role required");
	        }
	        return ResponseEntity.ok(productService.addProduct(productDto));
	}

	 @PutMapping("/{id}")
	    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authHeader, @Valid @RequestBody ProductDto productDto) {
	        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
	        if (token == null || !jwtService.hasAdminRole(token)) {
	            return ResponseEntity.status(403).body("Unauthorized: Admin role required");
	        }
	        Optional<ProductDto> updatedProduct = productService.updateProduct(id, productDto);
	        if (updatedProduct.isPresent()) {
	            return ResponseEntity.ok(updatedProduct.get());
	        }
	        return ResponseEntity.status(404).body("Product with ID " + id + " not found.");
	    }

	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        if (token == null || !jwtService.hasAdminRole(token)) {
            return ResponseEntity.status(403).body("Unauthorized: Admin role required");
        }
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(404).body("Product with ID " + id + " not found.");
    }
	
	@PatchMapping("/{id}/stock")
	 public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody int quantity) {
        return productService.updateStock(id, quantity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Product with ID " + id + " not found."));
    
    }

    @PatchMapping("/{id}/stock/increase")
    public ResponseEntity<?> increaseStock(@PathVariable Long id, @RequestBody int quantity) {
        return productService.increaseStock(id, quantity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Product with ID " + id + " not found."));
    }
}
