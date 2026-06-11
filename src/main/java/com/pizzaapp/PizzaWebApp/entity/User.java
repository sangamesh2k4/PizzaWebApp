package com.pizzaapp.PizzaWebApp.entity;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "user")
public class User {

    //private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @Indexed(unique = true)
    @Id
    @Email(message = "Invalid email format")
    private String email;

   @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters and contain a number"
    )
    private String password;
    private String role;
    private boolean enabled;

    // Constructors
    public User() {}
    private List<MenuItem> cart = new ArrayList<>();

    public User(String name, String email, String password, String role ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled=true;
    }

    // Getters and Setters
//    public String  getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    // Manual Getter for Cart
    public List<MenuItem> getCart() {
        return cart;
    }

    // Manual Setter for Cart
    public void setCart(List<MenuItem> cart) {
        this.cart = cart;
    }
}
