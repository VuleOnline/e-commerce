package com.vule.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vule.user_service.entities.MyUser;

@Repository
public interface MyUserRepo extends JpaRepository<MyUser, Long>{

	Optional<MyUser> findByUsername(String username);
}
