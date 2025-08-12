package com.vule.user_service.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vule.user_service.dto.AddressDto;
import com.vule.user_service.dto.UserDto;
import com.vule.user_service.entities.MyUser;
import com.vule.user_service.repository.MyUserRepo;


@Service
public class UserService {
	
	private final MyUserRepo userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(MyUserRepo userRepository) {
        this.userRepository = userRepository;
    }
    
    public Optional<UserDto> getUserByUsername(String username) {
        logger.info("Fetching user with username {}", username);
        Optional<MyUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            logger.error("User with username {} not found", username);
            return Optional.empty();
        }
        MyUser user = userOpt.get();
        AddressDto address = new AddressDto();
        address.setCity(user.getAddress().getCity()); 
        address.setStreet(user.getAddress().getStreet());
        address.setPostalCode(user.getAddress().getPostalCode());
        address.setCountry(user.getAddress().getCountry());
        
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setAddress(address);
        userDto.setPhone(user.getPhone());
        return Optional.of(userDto);
    }
}


