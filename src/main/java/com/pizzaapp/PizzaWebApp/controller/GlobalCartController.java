package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.service.CartService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.pizzaapp.PizzaWebApp.model.Cart;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalCartController {
    @Autowired private final CartService cartService;

    @ModelAttribute
    public void globalAttributes( Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth== null|| !auth.isAuthenticated()|| "anonymousUser".equals(auth.getPrincipal())){
            model.addAttribute("cart",new Cart());
            return;
        }
        String email= auth.getName();
        Cart cart=cartService.getCart(email);
        model.addAttribute("cart",cart);

    }
}