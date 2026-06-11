package com.pizzaapp.PizzaWebApp.filter;

import com.pizzaapp.PizzaWebApp.service.CustomUserDetailsService;
import com.pizzaapp.PizzaWebApp.service.JwtService;
import com.pizzaapp.PizzaWebApp.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import jakarta.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueWhenNoTokenPresent() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain =
                new MockFilterChain();

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                chain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }

    @Test
    void shouldAuthenticateValidToken() throws Exception {

        String token = "valid-token";

        var userDetails =
                User.withUsername("test@gmail.com")
                        .password("password")
                        .authorities("USER")
                        .build();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setCookies(
                new Cookie(
                        "accessToken",
                        token
                )
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain =
                new MockFilterChain();

        when(jwtService.extractUsername(token))
                .thenReturn("test@gmail.com");

        when(customUserDetailsService.loadUserByUsername(
                "test@gmail.com"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(
                token,
                userDetails))
                .thenReturn(true);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                chain
        );

        assertNotNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }
    @Test
    void shouldNotAuthenticateInvalidToken() throws Exception {

        String token = "invalid-token";

        var userDetails =
                User.withUsername("test@gmail.com")
                        .password("password")
                        .authorities("USER")
                        .build();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setCookies(
                new Cookie(
                        "accessToken",
                        token
                )
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain =
                new MockFilterChain();

        when(jwtService.extractUsername(token))
                .thenReturn("test@gmail.com");

        when(customUserDetailsService.loadUserByUsername(
                "test@gmail.com"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(
                token,
                userDetails))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                chain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }}