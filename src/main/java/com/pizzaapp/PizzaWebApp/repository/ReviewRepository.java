package com.pizzaapp.PizzaWebApp.repository;

import com.pizzaapp.PizzaWebApp.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    // Spring Boot will automatically implement common methods like findAll()
}