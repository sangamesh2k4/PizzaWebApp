package com.pizzaapp.PizzaWebApp.repository;

import com.pizzaapp.PizzaWebApp.entity.PendingOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PendingOrderRepository extends MongoRepository <PendingOrder,String>{
    Optional<PendingOrder> findByUserEmail(
            String userEmail
    );
}
