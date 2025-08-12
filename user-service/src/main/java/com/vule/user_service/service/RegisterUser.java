package com.vule.user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vule.user_service.dto.UserDto;
import com.vule.user_service.entities.Address;
import com.vule.user_service.entities.MyUser;
import com.vule.user_service.repository.MyUserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterUser {
	
	    private final MyUserRepo userRepo;
	    private final PasswordEncoder passwordEncoder;

	    public void registerUser(UserDto userDto) {
	    	Address address = new Address();
	        address.setStreet(userDto.getAddress().getStreet());
	        address.setCity(userDto.getAddress().getCity());
	        address.setPostalCode(userDto.getAddress().getPostalCode());
	        address.setCountry(userDto.getAddress().getCountry());
	    	
	    	MyUser user = new MyUser();
	        user.setFirstName(userDto.getFirstName());
	        user.setLastName(userDto.getLastName());
	        user.setUsername(userDto.getUsername());
	        user.setEmail(userDto.getEmail());
	        user.setAddress(address);
	        user.setPhone(userDto.getPhone());
	        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
	        user.setRole("ROLE_USER");
	        userRepo.save(user);
	    }

}
