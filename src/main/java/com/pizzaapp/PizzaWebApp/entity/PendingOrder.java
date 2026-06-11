package com.pizzaapp.PizzaWebApp.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "pending_orders")
public class PendingOrder {

    @Id
    private String id;

    private String userEmail;

    private String customerName;

    private String address;

    private Double grandTotal;

    private Date createdAt;
}
