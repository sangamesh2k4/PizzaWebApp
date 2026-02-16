package com.pizzaapp.PizzaWebApp.repository;

import com.pizzaapp.PizzaWebApp.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {
}
