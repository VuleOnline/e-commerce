package com.vule.cart_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vule.cart_service.dto.CartItemDto;
import com.vule.cart_service.dto.ProductDto;
import com.vule.cart_service.feign.ProductClient;

import io.jsonwebtoken.JwtException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CartService {
	
	private final ProductClient productClient;
	private final RedisTemplate<String, Object> redisTemplate;
	 private final JwtService jwtService;
	private static final String CART_KEY_PREFIX = "cart:";
	private static final Logger logger = LoggerFactory.getLogger(CartService.class);
	
	@Transactional
	public void addToCart(String username, Long productId, int quantity) {
		ResponseEntity<ProductDto> response = productClient.getProductById(productId);
		if(!response.getStatusCode().is2xxSuccessful()) {
			throw new IllegalArgumentException("Product with ID " + productId + " does not exist");
		}
		 ProductDto product = response.getBody();
	     if (product == null) {
	         throw new IllegalArgumentException("Product data not available for ID " + productId);
	    }
		if(product.getStock()< quantity) {
			throw new IllegalArgumentException("Insufficient stock for product ID " + productId);
		}
		ResponseEntity<String> stockResponse = productClient.updateStock(productId, quantity);
        if (!stockResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException(stockResponse.getBody());
        }
        
        String cartKey = CART_KEY_PREFIX + username;
        CartItemDto cartItem = new CartItemDto();
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        
        List<CartItemDto> cartItems = (List<CartItemDto>) redisTemplate.opsForValue().get(cartKey);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        cartItems.add(cartItem);
        updateCartInRedis(cartKey, cartItems);
    }

	@Transactional
    public void removeFromCart(String username, Long productId) {
        String cartKey = CART_KEY_PREFIX + username;
        List<CartItemDto> cartItems = (List<CartItemDto>) redisTemplate.opsForValue().get(cartKey);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty for user " + username);
        }
        CartItemDto itemToRemove = null;
        for (CartItemDto item : cartItems) {
            if (item.getProductId().equals(productId)) {
                itemToRemove = item;
                break;
            }
        }
        if (itemToRemove == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found in cart");
        }

        ResponseEntity<String> stockResponse = productClient.increaseStock(productId, itemToRemove.getQuantity());
        if (!stockResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException(stockResponse.getBody());
        }
        cartItems.remove(itemToRemove);
        updateCartInRedis(cartKey, cartItems);
        
    }
	
	private void updateCartInRedis(String cartKey, List<CartItemDto> cartItems) {
        try {
            if (cartItems.isEmpty()) {
                redisTemplate.delete(cartKey);
                logger.info("Deleted empty cart for key {} after removing item", cartKey);
            } else {
                redisTemplate.opsForValue().set(cartKey, cartItems);
                logger.info("Updated cart for key {} with {} items", cartKey, cartItems.size());
            }
        } catch (Exception e) {
            logger.error("Failed to update Redis for cartKey {}: {}", cartKey, e.getMessage());
            throw new IllegalStateException("Failed to update cart in Redis");
        }
    }
	
	public List<CartItemDto> getCart(String token) {
        String username;
        try {
            if (token != null) {
                username = jwtService.extractUsername(token);
            } else {
                username = UUID.randomUUID().toString();
            }
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid or expired JWT token");
        }

        String cartKey = CART_KEY_PREFIX + username;
        logger.info("Fetching cart for user: {}", username);
        List<CartItemDto> cartItems = (List<CartItemDto>) redisTemplate.opsForValue().get(cartKey);
        return cartItems != null ? cartItems : new ArrayList<>();
    }
		
	

}
