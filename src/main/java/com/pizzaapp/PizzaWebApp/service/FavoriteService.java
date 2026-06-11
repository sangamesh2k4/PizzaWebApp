package com.pizzaapp.PizzaWebApp.service;

import com.pizzaapp.PizzaWebApp.entity.FavoriteEntity;
import com.pizzaapp.PizzaWebApp.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public Set<String> getFavorites(
            String email){

        return favoriteRepository
                .findByUserEmail(email)
                .map(FavoriteEntity::getFavoriteIds)
                .orElse(new HashSet<>());
    }

    public void saveFavorites(
            String email,
            Set<String> favorites){

        FavoriteEntity entity =
                favoriteRepository
                        .findByUserEmail(email)
                        .orElse(new FavoriteEntity());

        entity.setUserEmail(email);
        entity.setFavoriteIds(favorites);

        favoriteRepository.save(entity);
    }
}
