package com.pizzaapp.PizzaWebApp.entity;

import com.pizzaapp.PizzaWebApp.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartEntity {

    @Id
    private String id;

    private String userEmail;

    private Cart cart;
}
