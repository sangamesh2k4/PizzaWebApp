package com.pizzaapp.PizzaWebApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {

    @GetMapping("/authlogin")
    public String login() {
        return "login";
    }
    @GetMapping("/register_authpage")
    public String registerPage() {
        return "register";
    }
}
