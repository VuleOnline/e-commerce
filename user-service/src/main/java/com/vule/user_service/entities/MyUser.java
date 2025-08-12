package com.vule.user_service.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.*;


@Entity
@Table(name = "User")
@Data
public class MyUser {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String firstName;

    private String lastName;
    
    private String password;
    
    private String email;
    
    private String role;

    @Column
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

}
