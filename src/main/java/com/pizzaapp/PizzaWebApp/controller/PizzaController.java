package com.pizzaapp.PizzaWebApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import com.pizzaapp.PizzaWebApp.service.PizzaService;

@Controller
@RequestMapping("/pizzas")
public class PizzaController {

    @Autowired
    private PizzaService pizzaService;

    @GetMapping
    public String getAllPizzas(Model model) {

        List<MenuItem> menuItems = pizzaService.getAllPizzas();
        System.out.println("Pizzas count = " + menuItems.size());
        model.addAttribute("pizzas", menuItems);
        model.addAttribute("pizza", new MenuItem());

        return "pizzas"; // loads pizzas.html
    }
}

