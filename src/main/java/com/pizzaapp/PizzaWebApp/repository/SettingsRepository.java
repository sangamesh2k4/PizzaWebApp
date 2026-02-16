package com.pizzaapp.PizzaWebApp.repository;

import com.pizzaapp.PizzaWebApp.entity.StoreSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends MongoRepository<StoreSettings, String> {
}