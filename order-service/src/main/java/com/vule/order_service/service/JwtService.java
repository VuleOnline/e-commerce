package com.vule.order_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
	
	@Value("${jwt.secret}")
    private String secretKey;
	
	public String extractUsername(String token) {
	        try {
	            Claims claims = Jwts.parser()
	                    .setSigningKey(secretKey)
	                    .parseClaimsJws(token)
	                    .getBody();
	            return claims.getSubject();
	        } catch (JwtException e) {
	            throw new JwtException("Invalid or expired JWT token");
	        }
	    }

}
