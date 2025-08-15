package com.vule.order_service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vule.order_service.service.JwtService;

import io.jsonwebtoken.JwtException;

@Aspect
@Component
public class Helper {

	private static final Logger logger = LoggerFactory.getLogger(Helper.class);
	private final JwtService jwtService;
	
	public Helper(JwtService jwtService) {
        this.jwtService = jwtService;
    }
	
	@Before("execution(* com.vule.order_service.service.OrderService.*(..))")
	public void logBefore(JoinPoint joinPoint) {
		logger.info("Starting Method:{} ", joinPoint.getSignature().getName());
		
	}
	
	@AfterReturning(pointcut = "execution(* com.vule.order_service.service.OrderService.*(..))", returning = "result")
	public void logAfter(JoinPoint joinPoint, Object result) {
		
		logger.info("Method completed:{}", joinPoint.getSignature().getName());
	}
	
	public void checkJwt(String jwt) {
		try {
            if (jwt != null) {
                String username = jwtService.extractUsername(jwt);
                logger.info("JWT valid for user: {}", username);
            }
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        }
		
	}
}
