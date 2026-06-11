package com.pizzaapp.PizzaWebApp.service;

import com.pizzaapp.PizzaWebApp.entity.CartEntity;
import com.pizzaapp.PizzaWebApp.model.Cart;
import com.pizzaapp.PizzaWebApp.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void shouldReturnExistingCart() {

        Cart cart = new Cart();

        CartEntity entity = new CartEntity();
        entity.setUserEmail("test@gmail.com");
        entity.setCart(cart);

        when(cartRepository.findByUserEmail("test@gmail.com"))
                .thenReturn(Optional.of(entity));

        Cart result =
                cartService.getCart("test@gmail.com");

        assertEquals(cart, result);
    }

    @Test
    void shouldReturnEmptyCartWhenNotFound() {

        when(cartRepository.findByUserEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        Cart result =
                cartService.getCart("test@gmail.com");

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void shouldSaveNewCart() {

        Cart cart = new Cart();

        when(cartRepository.findByUserEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        cartService.saveCart(
                "test@gmail.com",
                cart
        );

        verify(cartRepository)
                .save(any(CartEntity.class));
    }

    @Test
    void shouldUpdateExistingCart() {

        CartEntity entity = new CartEntity();
        entity.setUserEmail("test@gmail.com");

        Cart cart = new Cart();

        when(cartRepository.findByUserEmail("test@gmail.com"))
                .thenReturn(Optional.of(entity));

        cartService.saveCart(
                "test@gmail.com",
                cart
        );

        verify(cartRepository)
                .save(entity);
    }

    @Test
    void shouldSetEmailAndCartBeforeSaving() {

        Cart cart = new Cart();

        when(cartRepository.findByUserEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        cartService.saveCart(
                "test@gmail.com",
                cart
        );

        verify(cartRepository).save(
                argThat(entity ->
                        entity.getUserEmail()
                                .equals("test@gmail.com")
                                &&
                                entity.getCart() == cart
                )
        );
    }
}