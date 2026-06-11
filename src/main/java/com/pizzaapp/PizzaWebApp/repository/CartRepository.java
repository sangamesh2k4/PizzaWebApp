package com.pizzaapp.PizzaWebApp.repository;

import com.pizzaapp.PizzaWebApp.entity.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository
        extends MongoRepository<CartEntity,String> {

    Optional<CartEntity> findByUserEmail(
            String userEmail
    );
}
