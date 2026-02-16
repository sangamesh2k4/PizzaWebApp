package com.pizzaapp.PizzaWebApp.repository;

import com.pizzaapp.PizzaWebApp.entity.ContactMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactRepository extends MongoRepository<ContactMessage, String> {
    // Basic CRUD is enough for now
}