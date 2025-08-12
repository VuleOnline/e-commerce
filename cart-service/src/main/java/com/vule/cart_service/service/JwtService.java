package com.vule.cart_service.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    public String extractUsername(String jwt) {
    	return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }
}
