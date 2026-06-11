package com.pizzaapp.PizzaWebApp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "favorites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteEntity {

    @Id
    private String id;

    private String userEmail;

    private Set<String> favoriteIds =
            new HashSet<>();
}
