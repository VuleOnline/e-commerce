package com.vule.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.vule.order_service.dto.UserDto;

@FeignClient(name = "user-service")
public interface UserClient {
	
	@GetMapping("/user/{username}")
    ResponseEntity<UserDto> getUserByUsername(@PathVariable String username, @RequestHeader("Authorization") String authHeader);

}
