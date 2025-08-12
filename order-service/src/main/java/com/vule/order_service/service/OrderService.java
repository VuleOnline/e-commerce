package com.vule.order_service.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vule.order_service.dto.AddressDto;
import com.vule.order_service.dto.CartItemDto;
import com.vule.order_service.dto.DeliveryInfoDto;
import com.vule.order_service.dto.OrderRequestDto;
import com.vule.order_service.dto.OrderResponseDto;
import com.vule.order_service.dto.ProductDto;
import com.vule.order_service.dto.UserDto;
import com.vule.order_service.entity.DeliveryInfo;
import com.vule.order_service.entity.Order;
import com.vule.order_service.entity.OrderItem;
import com.vule.order_service.feign.CartClient;
import com.vule.order_service.feign.ProductClient;
import com.vule.order_service.feign.UserClient;
import com.vule.order_service.mapper.OrderMapper;
import com.vule.order_service.repo.OrderRepository;

import io.jsonwebtoken.JwtException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;

@Service
public class OrderService {
	
			private final OrderRepository orderRepository;
			private final UserClient userClient;
			private final CartClient cartClient;
			private final ProductClient productClient;
			private final OrderMapper orderMapper;
			private final JwtService jwtService;
			private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	 	
	 	public OrderService(OrderRepository orderRepository, UserClient userClient, CartClient cartClient,
                ProductClient productClient, OrderMapper orderMapper, JwtService jwtService) {
	 			this.orderRepository = orderRepository;
	 			this.userClient = userClient;
	 			this.cartClient = cartClient;
	 			this.productClient = productClient;
	 			this.orderMapper = orderMapper;
	 			this.jwtService = jwtService;
	 	}

	 	@Transactional
	 	public OrderResponseDto createOrder(String token, DeliveryInfoDto deliveryInfoDto) {
	 		String username;
	 		try {
	 			if (token != null) {
	 				username = jwtService.extractUsername(token);
	 			} else {
	 				username = UUID.randomUUID().toString();
	 			}
	 		} catch (JwtException e) {
	 			logger.error("Invalid JWT token: {}", e.getMessage());
	 			throw e;
	 		}
	 		logger.info("Creating order for user: {}", username);

	 		
	 		String authHeader = token != null ? "Bearer " + token : null;
	 		ResponseEntity<List<CartItemDto>> cartResponse = cartClient.getCart(authHeader);
	 		if (!cartResponse.getStatusCode().is2xxSuccessful() 
	 				|| cartResponse.getBody() == null || cartResponse.getBody().isEmpty()) {
	 		logger.error("Cart is empty for user: {}", username);
	 		throw new IllegalArgumentException("Cart is empty");
	 		}
	 		List<CartItemDto> cartItems = cartResponse.getBody();

// Proveri stanje proizvoda i keširaj ProductDto
	 		Map<Long, ProductDto> productCache = new HashMap<>();
	 		for (CartItemDto item : cartItems) {
	 			ResponseEntity<ProductDto> productResponse = productClient.getProductById(item.getProductId());
	 			if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
	 				logger.error("Product not found: {}", item.getProductId());
	 				throw new IllegalArgumentException("Product with ID " + item.getProductId() + " not found");
    }
	 			ProductDto product = productResponse.getBody();
	 			productCache.put(item.getProductId(), product);
	 			if (product.getStock() < item.getQuantity()) {
	 				logger.error("Insufficient stock for product: {}", item.getProductId());
	 				throw new IllegalArgumentException("Insufficient stock for product ID " + item.getProductId());
    }
    
}

// Dohvati podatke o dostavi
	 	DeliveryInfo deliveryInfo = new DeliveryInfo();
	 	if (username != null && !username.equals("guest")) {
	 		ResponseEntity<UserDto> userResponse = userClient.getUserByUsername(username, authHeader);
	 		if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
	 			logger.error("User not found: {}", username);
	 			throw new IllegalArgumentException("User not found");
	 		}
	 	UserDto user = userResponse.getBody();
	 	AddressDto address = new AddressDto();
	 	address.setCountry(user.getAddress().getCountry());
	 	address.setCity(user.getAddress().getCity());
	 	address.setPostalCode(user.getAddress().getPostalCode());
	 	address.setStreet(user.getAddress().getStreet());
	 	
		deliveryInfo.setFirstName(user.getFirstName());
		deliveryInfo.setLastName(user.getLastName());
		deliveryInfo.setAddress(address);
		deliveryInfo.setPhone(user.getPhone());
	} else {
		AddressDto address = new AddressDto();
		address.setCountry(deliveryInfoDto.getAddress().getCountry());
		address.setCity(deliveryInfoDto.getAddress().getCity());
		address.setPostalCode(deliveryInfoDto.getAddress().getPostalCode());
		address.setStreet(deliveryInfoDto.getAddress().getStreet());
		
		deliveryInfo.setFirstName(deliveryInfoDto.getFirstName());
		deliveryInfo.setLastName(deliveryInfoDto.getLastName());
		deliveryInfo.setAddress(address);
		deliveryInfo.setPhone(deliveryInfoDto.getPhone());
}

// Kreiraj porudžbinu
	 	Order order = new Order();
	 	order.setUserId(deliveryInfo.getId()); // <-- koristi Long userId; ako imaš samo username, zameni ili konvertuj

	 	List<OrderItem> items = cartItems.stream()
	 	    .map(item -> {
	 	        ProductDto product = productCache.get(item.getProductId());
	 	        if (product == null) {
	 	            throw new IllegalStateException("Product not found: " + item.getProductId());
	 	        }
	 	        OrderItem oi = new OrderItem();
	 	        oi.setProductId(item.getProductId());
	 	        oi.setPrice(product.getPrice());
	 	        oi.setQuantity(item.getQuantity());
	 	        oi.setOrder(order);
	 	        return oi;
	 	    })
	 	    .collect(Collectors.toList());

	 	order.setItems(items);

	 	// deliveryInfo -> polja (prilagodi nazive metoda na deliveryInfo objektu)
	 	order.setDeliveryName(deliveryInfo.getFirstName());
	 	order.setDeliveryLastName(deliveryInfo.getLastName());
	 	order.setDeliveryAddress(deliveryInfo.getAddress());
	 	order.setDeliveryPhone(deliveryInfo.getPhone());

	 	double total = items.stream()
	 	    .mapToDouble(i -> i.getPrice() * i.getQuantity())
	 	    .sum();
	 	order.setTotalPrice(total);

	 	order.setStatus("PENDING");
	 	order.setCreatedAt(LocalDateTime.now());

	 	Order savedOrder = orderRepository.save(order);
	 	logger.info("Order saved with ID: {}", savedOrder.getId());

	 	return orderMapper.toDto(savedOrder);
}
}
