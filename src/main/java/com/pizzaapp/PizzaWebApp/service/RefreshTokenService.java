package com.pizzaapp.PizzaWebApp.service;


import com.pizzaapp.PizzaWebApp.entity.RefreshToken;
import com.pizzaapp.PizzaWebApp.exception.InvalidRefreshTokenException;
import com.pizzaapp.PizzaWebApp.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken saveRefreshToken(String token,String email){
        RefreshToken refreshToken=RefreshToken.builder()
                .token(token)
                .userEmail(email)
                .expiryDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken=refreshTokenRepository.findByToken(token)
                .orElseThrow(()->new InvalidRefreshTokenException("refresh token not found"));

        if(refreshToken.isRevoked()){
            throw new InvalidRefreshTokenException("refresh token is revoked");
        }
        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            throw new InvalidRefreshTokenException("refresh token is expired");
        }
        return refreshToken;
    }
}
