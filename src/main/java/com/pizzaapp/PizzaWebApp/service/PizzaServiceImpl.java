package com.pizzaapp.PizzaWebApp.service;

import java.util.List;

import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pizzaapp.PizzaWebApp.repository.MenuItemRepository;

@Service
public class PizzaServiceImpl implements PizzaService {

    @Autowired
    private MenuItemRepository pizzaRepository;

    @Override
    public List<MenuItem> getAllPizzas() {
        return pizzaRepository.findAll();
    }

    @Override
    public void savePizza(MenuItem menuItem) {
        menuItem.setAvailable(true);
        if (menuItem.getCategory() == null || menuItem.getCategory().isEmpty()) {
            menuItem.setCategory("Pizza");
        }
        if (menuItem.getImageUrl() == null || menuItem.getImageUrl().isEmpty()) {
            menuItem.setImageUrl("https://via.placeholder.com/300"); // Generic placeholder
        }
        pizzaRepository.save(menuItem);
    }

    @Override
    public void disablePizza(String id) {
        MenuItem menuItem = pizzaRepository.findById(id).orElseThrow();
        menuItem.setAvailable(false);
        pizzaRepository.save(menuItem);
    }

    @Override
    public void enablePizza(String id) {
        MenuItem menuItem = pizzaRepository.findById(id).orElseThrow();
        menuItem.setAvailable(true);
        pizzaRepository.save(menuItem);
    }
}

