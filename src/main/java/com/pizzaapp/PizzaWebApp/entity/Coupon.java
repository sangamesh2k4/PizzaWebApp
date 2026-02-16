package com.pizzaapp.PizzaWebApp.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "coupons")
public class Coupon {
    @Id
    private String id;
    private String code;        // e.g., "SAVE30"
    private int discountPercent; // e.g., 30
    private boolean active;      // true/false
}