package com.pizzaapp.PizzaWebApp.service;


import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp(){
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService,"secretKey","my-super-secret-jwt-key-for-pizza-app-project-2026");
        ReflectionTestUtils.setField(jwtService,"accessExpiration",60000L);
        ReflectionTestUtils.setField(jwtService,"refreshExpiration",300000L);
        userDetails =User.withUsername("test@gmail.com").password("password").authorities("USER").build();

    }
    @Test
    void shouldExtractUsername() {
        String token = jwtService.generateAccessToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("test@gmail.com", username);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtService.generateAccessToken(userDetails);
        boolean valid = jwtService.isTokenValid(token, userDetails);
        assertTrue(valid);
    }

    @Test
    void shouldDetectExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "accessExpiration", 1L);
        String token = jwtService.generateAccessToken(userDetails);
        Thread.sleep(10);
        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUsername(token));
    }

    @Test
    void shouldRejectWrongUser() {
        String token = jwtService.generateAccessToken(userDetails);
        UserDetails anotherUser = User.withUsername("other@gmail.com").password("password").authorities("USER").build();
        assertFalse(jwtService.isTokenValid(token, anotherUser)
        );
    }
}
