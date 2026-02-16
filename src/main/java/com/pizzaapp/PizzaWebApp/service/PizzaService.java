package com.pizzaapp.PizzaWebApp.service;

import java.util.List;

import com.pizzaapp.PizzaWebApp.entity.MenuItem;

public interface PizzaService {

    List<MenuItem> getAllPizzas();

    void savePizza(MenuItem menuItem);

    void disablePizza(String id);

    void enablePizza(String id);
}
