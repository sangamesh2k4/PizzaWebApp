package com.pizzaapp.PizzaWebApp.repository;

import com.pizzaapp.PizzaWebApp.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    // 🟢 Finds all orders for a specific email, sorted by newest first
    List<Order> findByEmailOrderByOrderDateDesc(String email);
}