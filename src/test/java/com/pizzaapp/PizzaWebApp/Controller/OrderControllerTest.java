package com.pizzaapp.PizzaWebApp.Controller;

import com.pizzaapp.PizzaWebApp.controller.OrderController;
import com.pizzaapp.PizzaWebApp.entity.Order;
import com.pizzaapp.PizzaWebApp.repository.OrderRepository;
import com.pizzaapp.PizzaWebApp.repository.SettingsRepository;
import com.pizzaapp.PizzaWebApp.service.CartService;
import com.pizzaapp.PizzaWebApp.service.EmailService;
import com.pizzaapp.PizzaWebApp.service.PdfService;
import com.pizzaapp.PizzaWebApp.service.JwtService;
import com.pizzaapp.PizzaWebApp.filter.JwtAuthenticationFilter;
import com.pizzaapp.PizzaWebApp.entrypoint.JwtAuthenticationEntryPoint;
import com.pizzaapp.PizzaWebApp.service.CustomUserDetailsService;
import com.pizzaapp.PizzaWebApp.service.RefreshTokenService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {
    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private PdfService pdfService;

    @MockitoBean
    private CartService cartService;


    @MockitoBean
    private SettingsRepository settingsRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;
    @Test
    @WithMockUser(username = "test@gmail.com")
    void shouldLoadUserOrders() throws Exception {

        Order order = new Order();
        order.setEmail("test@gmail.com");

        when(orderRepository.findByEmailOrderByOrderDateDesc(
                "test@gmail.com"))
                .thenReturn(List.of(order));

        mockMvc.perform(get("/orders")
                        .principal(
                                () -> "test@gmail.com"
                        ))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void shouldHandleEmptyOrders() throws Exception {

        when(orderRepository.findByEmailOrderByOrderDateDesc(
                "test@gmail.com"))
                .thenReturn(List.of());

        mockMvc.perform(get("/orders")
                        .principal(
                                () -> "test@gmail.com"
                        ))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"));
    }

    @Test
    void shouldTrackOrder() throws Exception {

        Order order = new Order();
        order.setId("123");

        when(orderRepository.findById("123"))
                .thenReturn(
                        java.util.Optional.of(order)
                );

        mockMvc.perform(
                        get("/order/track")
                                .param("orderId","123")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("track-order"));
    }
    @Test
    void shouldCancelPlacedOrder() throws Exception {

        Order order = new Order();
        order.setId("123");
        order.setStatus("PLACED");

        when(orderRepository.findById("123"))
                .thenReturn(
                        java.util.Optional.of(order)
                );

        mockMvc.perform(
                        post("/order/cancel")
                                .param("orderId","123")
                )
                .andExpect(status().is3xxRedirection());

        verify(orderRepository)
                .save(order);
    }
}
