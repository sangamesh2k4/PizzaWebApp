package com.pizzaapp.PizzaWebApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.pizzaapp.PizzaWebApp.entity.User;
import com.pizzaapp.PizzaWebApp.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;


    // ✅ Handle Register Form Submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/login"; // redirect to login3 after registration
    }
}
