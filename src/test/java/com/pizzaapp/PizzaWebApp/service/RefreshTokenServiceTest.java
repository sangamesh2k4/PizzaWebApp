package com.pizzaapp.PizzaWebApp.service;

import com.pizzaapp.PizzaWebApp.entity.RefreshToken;
import com.pizzaapp.PizzaWebApp.exception.InvalidRefreshTokenException;
import com.pizzaapp.PizzaWebApp.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldSaveRefreshToken() {

        RefreshToken savedToken =
                RefreshToken.builder().token("token123").userEmail("test@gmail.com").revoked(false).expiryDate(
                                Instant.now().plusSeconds(3600)
                        ).build();

        when(refreshTokenRepository.save(any())).thenReturn(savedToken);

        RefreshToken result =
                refreshTokenService.saveRefreshToken("token123", "test@gmail.com");

        assertNotNull(result);
        assertEquals("token123", result.getToken());
        assertEquals("test@gmail.com", result.getUserEmail());
    }

    @Test
    void shouldVerifyValidRefreshToken() {

        RefreshToken refreshToken =
                RefreshToken.builder().token("valid-token").userEmail("test@gmail.com").revoked(false)
                        .expiryDate(
                                Instant.now().plusSeconds(3600)
                        )
                        .build();

        when(refreshTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.verifyRefreshToken("valid-token");

        assertEquals(
                "valid-token",
                result.getToken()
        );
    }

    @Test
    void shouldThrowWhenTokenNotFound() {

        when(refreshTokenRepository.findByToken("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> refreshTokenService.verifyRefreshToken(
                        "missing"
                )
        );
    }

    @Test
    void shouldThrowWhenTokenRevoked() {
        RefreshToken refreshToken = RefreshToken.builder().token("revoked-token").revoked(true).expiryDate(
                Instant.now().plusSeconds(3600))
                        .build();

        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(refreshToken));
        assertThrows(
                InvalidRefreshTokenException.class,
                () -> refreshTokenService.verifyRefreshToken(
                        "revoked-token"
                )
        );
    }

    @Test
    void shouldThrowWhenTokenExpired() {
        RefreshToken refreshToken = RefreshToken.builder().token("expired-token").revoked(false).expiryDate(
                                Instant.now().minusSeconds(60)
                        )
                        .build();

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(refreshToken));

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> refreshTokenService.verifyRefreshToken(
                        "expired-token"
                )
        );
    }
}
