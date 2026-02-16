package com.pizzaapp.PizzaWebApp.repository;

import com.pizzaapp.PizzaWebApp.entity.Coupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CouponRepository extends MongoRepository<Coupon, String> {
    // 🟢 Custom query to find a coupon by its code
    Optional<Coupon> findByCode(String code);
}