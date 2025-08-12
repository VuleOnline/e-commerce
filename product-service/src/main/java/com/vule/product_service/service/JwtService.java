package com.vule.product_service.service;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    public boolean isTokenValid(String jwt, String username) {
        final String extractedUsername = extractUsername(jwt);
        return (extractedUsername.equals(username) && !isTokenExpired(jwt));
    }

    private boolean isTokenExpired(String jwt) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String extractRole(String jwt) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(jwt)
            .getBody()
            .get("role", String.class);
        }
    public boolean hasAdminRole(String jwt) {
        String role = extractRole(jwt);
        return "ROLE_ADMIN".equals(role);
    }
}
