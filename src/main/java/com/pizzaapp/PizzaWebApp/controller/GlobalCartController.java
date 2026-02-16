package com.pizzaapp.PizzaWebApp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.pizzaapp.PizzaWebApp.model.Cart;

@ControllerAdvice
public class GlobalCartController {

    @ModelAttribute
    public void globalAttributes(HttpSession session, Model model) {
        // 1. Get the raw object from session
        Object sessionObj = session.getAttribute("cart");

        Cart cart = null;

        // 2. SAFETY CHECK: Is this actually a Cart object?
        if (sessionObj instanceof Cart) {
            // Yes, it's safe to cast
            cart = (Cart) sessionObj;
        } else {
            // No, it's either null OR the old broken ArrayList.
            // Create a fresh new Cart to prevent the crash.
            cart = new Cart();

            // Overwrite the bad data in the session immediately
            session.setAttribute("cart", cart);
        }

        // 3. Add to model so HTML can use it
        model.addAttribute("cart", cart);
    }
}