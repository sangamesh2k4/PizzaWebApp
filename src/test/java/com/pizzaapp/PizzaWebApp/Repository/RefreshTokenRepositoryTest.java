package com.pizzaapp.PizzaWebApp.Repository;

import com.pizzaapp.PizzaWebApp.entity.RefreshToken;
import com.pizzaapp.PizzaWebApp.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void cleanDatabase() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    void shouldFindRefreshTokenByToken() {

        RefreshToken token =
                RefreshToken.builder()
                        .token("refresh-token-123")
                        .userEmail("test@gmail.com")
                        .expiryDate(
                                Instant.now().plusSeconds(3600)
                        )
                        .revoked(false)
                        .build();

        refreshTokenRepository.save(token);

        Optional<RefreshToken> result =
                refreshTokenRepository.findByToken(
                        "refresh-token-123"
                );

        assertTrue(result.isPresent());

        assertEquals(
                "test@gmail.com",
                result.get().getUserEmail()
        );
    }

    @Test
    void shouldReturnEmptyWhenTokenNotFound() {

        Optional<RefreshToken> result =
                refreshTokenRepository.findByToken(
                        "missing-token"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteByUserEmail() {

        RefreshToken token =
                RefreshToken.builder()
                        .token("refresh-token-456")
                        .userEmail("delete@gmail.com")
                        .expiryDate(
                                Instant.now().plusSeconds(3600)
                        )
                        .revoked(false)
                        .build();

        refreshTokenRepository.save(token);

        refreshTokenRepository.deleteByUserEmail(
                "delete@gmail.com"
        );

        Optional<RefreshToken> result =
                refreshTokenRepository.findByToken(
                        "refresh-token-456"
                );

        assertTrue(result.isEmpty());
    }
}