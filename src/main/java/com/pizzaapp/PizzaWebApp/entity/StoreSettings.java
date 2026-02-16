package com.pizzaapp.PizzaWebApp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "settings")
public class StoreSettings {
    @Id
    private String id = "global_config"; // Only one document ever
    private boolean storeOpen = true;

    // Getters and Setters



    public boolean isStoreOpen() {
        return storeOpen;
    }

    public void setStoreOpen(boolean storeOpen) {
        this.storeOpen = storeOpen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
