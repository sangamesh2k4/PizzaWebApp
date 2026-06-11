package com.pizzaapp.PizzaWebApp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/error-page")
    public String errorPage(
            Authentication authentication,
            Model model) {

        String homeUrl = "/login";

        if(authentication != null
                && authentication.isAuthenticated()) {

            boolean isAdmin =
                    authentication.getAuthorities()
                            .stream()
                            .anyMatch(a ->
                                    a.getAuthority()
                                            .equals("ADMIN"));

            homeUrl =
                    isAdmin
                            ? "/admin/dashboard"
                            : "/home";
        }

        model.addAttribute(
                "homeUrl",
                homeUrl
        );

        return "error";
    }
}
