package com.vule.user_service.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import com.vule.user_service.dto.JwtResponse;
import com.vule.user_service.dto.LoginUserDto;
import com.vule.user_service.dto.UserDto;
import com.vule.user_service.service.JwtService;
import com.vule.user_service.service.RegisterUser;
import com.vule.user_service.service.UserService;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	 @Autowired
	 private AuthenticationManager authenticationManager;
	 
	 @Autowired
	 private  RegisterUser registerUser;
	 
	 @Autowired
	 private JwtService jwtService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginUserDto loginUser) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword()));
            User user = (User) authentication.getPrincipal();
            String jwt = jwtService.generateToken(user);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("{\"error\": \"Invalid email or password\"}");
        }
	}
	 @PostMapping("/register")
	    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
		 registerUser.registerUser(userDto);
	        return ResponseEntity.ok("User registered successfully");
	    }
	 @PostMapping("/guest/token")
	    public ResponseEntity<?> generateGuestToken() {
	        String jwt = jwtService.generateGuestToken();
	        return ResponseEntity.ok(new JwtResponse(jwt));
	    }
	 
	 @GetMapping("/{username}")
	    public ResponseEntity<?> getUserByUsername(@PathVariable String username,
	    		@RequestHeader(value = "Authorization", required = false) String authHeader) {
	        try {
	            String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
	            if (token == null) {
	                return ResponseEntity.status(401).body("{\"error\": \"JWT token required\"}");
	            }
	            String tokenUsername = jwtService.extractUsername(token);
	            if (!username.equals(tokenUsername)) {
	                return ResponseEntity.status(403).body("{\"error\": \"Unauthorized: Username mismatch\"}");
	            }
	            Optional<UserDto> user = userService.getUserByUsername(username);
	            if (user.isPresent()) {
	                return ResponseEntity.ok(user.get());
	            }
	            return ResponseEntity.status(404).body("{\"error\": \"User with username " + username + " not found\"}");
	        } catch (JwtException e) {
	            return ResponseEntity.status(401).body("{\"error\": \"Invalid JWT token\"}");
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body("{\"error\": \"Internal server error\"}");
	        }
	    }
	    
		
	

}
