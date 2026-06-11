package com.pizzaapp.PizzaWebApp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



public class AccessDeniedController {


    @GetMapping("/access-denied")
    public String accessDenied(
            Authentication authentication,
            Model model) {

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ADMIN"));

        model.addAttribute(
                "homeUrl",
                isAdmin
                        ? "/admin/dashboard"
                        : "/home"
        );

        return "access-denied";
    }
    }

