package com.pizzaapp.PizzaWebApp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String customerName;
    private String orderId;
    private int rating; // 1 to 5
    private String comment;
    private Date date = new Date();

    // Standard Getters and Setters
}