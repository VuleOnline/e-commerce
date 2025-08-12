package com.vule.user_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vule.user_service.entities.MyUser;
import com.vule.user_service.repository.MyUserRepo;



@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    MyUserRepo repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<MyUser> user = repo.findByUsername(username);
		if(!user.isPresent()) 
		{
			throw new UsernameNotFoundException("User not found with email: " + username);
			
		}	
		MyUser userDB = user.get();
		return User.builder()
				.username(userDB.getUsername())
				.password(userDB.getPassword())
				.roles(userDB.getRole().replace("ROLE_", ""))
				.build();
	}
		
}

