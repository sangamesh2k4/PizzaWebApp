package com.pizzaapp.PizzaWebApp.repository;

import com.pizzaapp.PizzaWebApp.entity.FavoriteEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository
        extends MongoRepository<FavoriteEntity,String> {

    Optional<FavoriteEntity> findByUserEmail(
            String userEmail
    );
}
