package com.pizzaapp.PizzaWebApp.service;

import com.pizzaapp.PizzaWebApp.entity.FavoriteEntity;
import com.pizzaapp.PizzaWebApp.repository.FavoriteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @Test
    void shouldReturnFavoritesForUser() {

        Set<String> favorites =
                Set.of("pizza1", "pizza2");

        FavoriteEntity entity =
                new FavoriteEntity();

        entity.setUserEmail("test@gmail.com");
        entity.setFavoriteIds(favorites);

        when(
                favoriteRepository.findByUserEmail(
                        "test@gmail.com"
                )
        ).thenReturn(Optional.of(entity));

        Set<String> result =
                favoriteService.getFavorites(
                        "test@gmail.com"
                );

        assertEquals(2, result.size());
        assertTrue(result.contains("pizza1"));
    }

    @Test
    void shouldReturnEmptySetWhenUserHasNoFavorites() {

        when(
                favoriteRepository.findByUserEmail(
                        "test@gmail.com"
                )
        ).thenReturn(Optional.empty());

        Set<String> result =
                favoriteService.getFavorites(
                        "test@gmail.com"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSaveFavorites() {

        Set<String> favorites =
                new HashSet<>();

        favorites.add("pizza1");

        when(
                favoriteRepository.findByUserEmail(
                        "test@gmail.com"
                )
        ).thenReturn(Optional.empty());

        favoriteService.saveFavorites(
                "test@gmail.com",
                favorites
        );

        verify(favoriteRepository)
                .save(any(FavoriteEntity.class));
    }
}