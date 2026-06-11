package com.pizzaapp.PizzaWebApp.service;

import com.pizzaapp.PizzaWebApp.entity.User;
import com.pizzaapp.PizzaWebApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private void injectPasswordEncoder() throws Exception {
        Field field =
                UserServiceImpl.class
                        .getDeclaredField("passwordEncoder");

        field.setAccessible(true);
        field.set(
                userService,
                new BCryptPasswordEncoder()
        );
    }

    @Test
    void shouldSaveUserWithEncodedPassword() throws Exception {

        injectPasswordEncoder();

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("password123");

        userService.saveUser(user);

        verify(userRepository).save(user);

        assertNotEquals(
                "password123",
                user.getPassword()
        );
    }
    @Test
    void shouldAssignUserRoleWhenRoleIsNull()
            throws Exception {

        injectPasswordEncoder();

        User user = new User();
        user.setPassword("password");

        userService.saveUser(user);

        assertEquals(
                "USER",
                user.getRole()
        );
    }
    @Test
    void shouldEnableUserOnRegistration()
            throws Exception {

        injectPasswordEncoder();

        User user = new User();
        user.setPassword("password");

        userService.saveUser(user);

        assertTrue(
                user.isEnabled()
        );
    }
    @Test
    void shouldFindUserByEmail() {

        User user = new User();
        user.setEmail("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        User result =
                userService.findByEmailUser(
                        "test@gmail.com"
                );

        assertEquals(
                "test@gmail.com",
                result.getEmail()
        );
    }
    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepository.findByEmail("missing@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userService.findByEmailUser(
                        "missing@gmail.com"
                )
        );
    }
    @Test
    void shouldCheckEmailTaken() {

        when(userRepository.existsById("test@gmail.com"))
                .thenReturn(true);

        assertTrue(
                userService.isEmailTaken(
                        "test@gmail.com"
                )
        );
    }
}