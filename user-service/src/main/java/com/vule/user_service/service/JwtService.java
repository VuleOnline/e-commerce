package com.vule.user_service.service;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;



@Service
public class JwtService {
    @Value("${secretKey}")
    private String secretKey;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim("role", "ROLE_" + userDetails.getAuthorities().iterator().next().getAuthority())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

   

    public String generateGuestToken() {
        return Jwts.builder()
            .setSubject("guest_" + java.util.UUID.randomUUID().toString())
            .claim("role", "ROLE_GUEST")
            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public String extractUsername(String jwt) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(jwt)
            .getBody()
            .getSubject();
    }


    public String extractRole(String jwt) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(jwt)
            .getBody()
            .get("role", String.class);
    }

}
