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


    // Handle Register Form Submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if(userService.isEmailTaken(user.getEmail())){
            model.addAttribute("error","Customer with same email ID already exists");
            model.addAttribute("suggestion","try to sign up with different email or login with your existing ID");
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/login"; // redirect to login3 after registration
    }
}
