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


import jakarta.transaction.Transactional;

@Service
public class OrderService {
	
			private final OrderRepository orderRepository;
			private final UserClient userClient;
			private final CartClient cartClient;
			private final ProductClient productClient;
			private final OrderMapper orderMapper;

	 	
	 	public OrderService(OrderRepository orderRepository, UserClient userClient, CartClient cartClient,
                ProductClient productClient, OrderMapper orderMapper) {
	 			this.orderRepository = orderRepository;
	 			this.userClient = userClient;
	 			this.cartClient = cartClient;
	 			this.productClient = productClient;
	 			this.orderMapper = orderMapper;
	 	}

	 	@Transactional
	 	public OrderResponseDto createOrder(String token, DeliveryInfoDto deliveryInfoDto) {
	 		
	 		String username = (token != null) ? UUID.randomUUID().toString() : "guest";
	 		
	 		String authHeader = token != null ? "Bearer " + token : null;
	 		ResponseEntity<List<CartItemDto>> cartResponse = cartClient.getCart(authHeader);
	 		
	 		if (!cartResponse.getStatusCode().is2xxSuccessful() 
	 				|| cartResponse.getBody() == null || cartResponse.getBody().isEmpty()) {
	 			throw new IllegalArgumentException("Cart is empty");
	 		}
	 		List<CartItemDto> cartItems = cartResponse.getBody();

	 		Map<Long, ProductDto> productCache = new HashMap<>();
	 		for (CartItemDto item : cartItems) {
	 			ResponseEntity<ProductDto> productResponse = productClient.getProductById(item.getProductId());
	 			if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
	 				throw new IllegalArgumentException("Product with ID " + item.getProductId() + " not found");
    }
	 			ProductDto product = productResponse.getBody();
	 			productCache.put(item.getProductId(), product);
	 			if (product.getStock() < item.getQuantity()) {
	 				throw new IllegalArgumentException("Insufficient stock for product ID " + item.getProductId());
    }
    
}

	 	DeliveryInfo deliveryInfo = new DeliveryInfo();
	 	if (username != null && !username.equals("guest")) {
	 		ResponseEntity<UserDto> userResponse = userClient.getUserByUsername(username, authHeader);
	 		if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
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

	 	Order order = new Order();
	 	order.setUserId(deliveryInfo.getId()); 

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
	 	return orderMapper.toDto(savedOrder);
}
}
