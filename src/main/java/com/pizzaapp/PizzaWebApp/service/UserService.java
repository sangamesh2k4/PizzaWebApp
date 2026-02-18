package com.pizzaapp.PizzaWebApp.service;

import com.pizzaapp.PizzaWebApp.entity.User;

public interface UserService {
    void saveUser(User user);
    User findByEmailUser(String username);
    public boolean isEmailTaken(String email);
    }






