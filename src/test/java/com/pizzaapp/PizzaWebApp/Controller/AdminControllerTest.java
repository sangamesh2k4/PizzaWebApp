package com.pizzaapp.PizzaWebApp.Controller;

import com.pizzaapp.PizzaWebApp.config.CustomAccessDeniedHandler;
import com.pizzaapp.PizzaWebApp.config.SecurityConfig;
import com.pizzaapp.PizzaWebApp.config.CustomAuthenticationFailureHandler;
import com.pizzaapp.PizzaWebApp.config.CustomSuccessHandler;
import com.pizzaapp.PizzaWebApp.controller.AdminController;
import com.pizzaapp.PizzaWebApp.entrypoint.JwtAuthenticationEntryPoint;
import com.pizzaapp.PizzaWebApp.filter.JwtAuthenticationFilter;
import com.pizzaapp.PizzaWebApp.repository.*;
import com.pizzaapp.PizzaWebApp.service.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminController.class)
// 1. Import the REAL security config AND the REAL filter class
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Core Data Mocks ---
    @MockitoBean private MenuItemRepository menuItemRepository;
    @MockitoBean private OrderRepository orderRepository;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private ContactRepository contactRepository;
    @MockitoBean private ReviewRepository reviewRepository;
    @MockitoBean private SettingsRepository settingsRepo;
    @MockitoBean private EmailService emailService;
    @MockitoBean private CartService cartService;

    // --- Security Architecture Dependencies ---
    // 2. Mock JwtService instead of the filter itself!
    @MockitoBean private JwtService jwtService;
    @MockitoBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;
    @MockitoBean private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockitoBean private CustomSuccessHandler customSuccessHandler;
    @MockitoBean private RefreshTokenService refreshTokenService;
    @MockitoBean private CustomAccessDeniedHandler accessDeniedHandler;

    @Test
    // 3. Authority Safe-Guarding: Granting both formats avoids 403 mismatches
    @WithMockUser(username = "admin@gmail.com", authorities = {"ADMIN", "ROLE_ADMIN"})
    void adminShouldAccessDashboard() throws Exception {
        // Arrange
        when(userRepository.count()).thenReturn(10L);
        when(menuItemRepository.count()).thenReturn(20L);
        when(orderRepository.count()).thenReturn(5L);
        when(orderRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-dashboard"));
    }

    @Test
    void anonymousShouldRedirectToLogin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/admin/dashboard"))
                .andDo(print());
    }
}