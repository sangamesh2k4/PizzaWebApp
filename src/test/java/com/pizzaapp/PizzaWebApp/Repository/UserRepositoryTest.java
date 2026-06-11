package com.pizzaapp.PizzaWebApp.Repository;

import com.pizzaapp.PizzaWebApp.entity.User;
import com.pizzaapp.PizzaWebApp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setName("Test User");
        user.setPassword("password");

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByEmail(
                        "test@gmail.com"
                );

        assertTrue(result.isPresent());

        assertEquals("test@gmail.com", result.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {

        Optional<User> result =
                userRepository.findByEmail(
                        "missing@gmail.com"
                );

        assertTrue(result.isEmpty());

    }
}
