package com.pizzaapp.PizzaWebApp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, HttpSession session) {
        // 🟢 DEBUG PRINT 1: Did we reach here?
        System.out.println("🔥 LOGIN ATTEMPT RECEIVED: " + email);

        // 🟢 CRITICAL: Save to Session
        session.setAttribute("userEmail", email);

        // 🟢 DEBUG PRINT 2: Did it save?
        String saved = (String) session.getAttribute("userEmail");
        System.out.println("🔥 SESSION SAVED CHECK: " + saved);

        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}