package com.vule.cart_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vule.cart_service.dto.CartItemDto;
import com.vule.cart_service.dto.CartRequestDto;
import com.vule.cart_service.service.CartService;
import com.vule.cart_service.service.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {
	
	private final CartService cartService;
    private final JwtService jwtService;

    public CartController(CartService cartService, JwtService jwtService) {
        this.cartService = cartService;
        this.jwtService = jwtService;
    }
	
	 @PostMapping("/add")
	 public ResponseEntity<?> addToCart(@Valid @RequestBody CartRequestDto cartRequest, @RequestHeader(value = "Authorization",
	    required = false) String authHeader) {
		 try {
	            String jwt = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
	            if (jwt == null) {
	                return ResponseEntity.status(401).body("{\"error\": \"Missing or invalid Authorization header\"}");
	            }
	            String username = jwtService.extractUsername(jwt);
	            cartService.addToCart(username, cartRequest.getProductId(), cartRequest.getQuantity());
	            return ResponseEntity.ok("Item added to cart");
	        } catch (JwtException e) {
	            return ResponseEntity.status(401).body("{\"error\": \"Invalid JWT token\"}");
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(400).body("{\"error\": \"" + e.getMessage() + "\"}");
	        }
	    }
	 @DeleteMapping("/remove/{productId}")
	 public ResponseEntity<?> removeFromCart(@PathVariable Long productId, @RequestHeader(value = "Authorization", 
	    required = false) String authHeader) {
		 try {
	            String jwt = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
	            if (jwt == null) {
	                return ResponseEntity.status(401).body("{\"error\": \"Missing or invalid Authorization header\"}");
	            }
	            String username = jwtService.extractUsername(jwt);
	            cartService.removeFromCart(username, productId);
	            return ResponseEntity.ok("Item removed from cart");
	        } catch (JwtException e) {
	            return ResponseEntity.status(401).body("{\"error\": \"Invalid JWT token\"}");
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(400).body("{\"error\": \"" + e.getMessage() + "\"}");
	        }
	    }
	 
	 @GetMapping
	 public ResponseEntity<?> getCart(@RequestHeader(value = "Authorization", required = false) String authHeader) {
	        try {
	            String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
	            List<CartItemDto> cartItems = cartService.getCart(token);
	            return ResponseEntity.ok(cartItems);
	        } catch (JwtException e) {
	            return ResponseEntity.status(401).body("{\"error\": \"Invalid JWT token\"}");
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(400).body("{\"error\": \"" + e.getMessage() + "\"}");
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body("{\"error\": \"Internal server error\"}");
	        }
	    }

}
