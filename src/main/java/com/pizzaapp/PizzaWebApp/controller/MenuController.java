package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import com.pizzaapp.PizzaWebApp.repository.MenuItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;

@Controller
public class MenuController {

    @Autowired
    private MenuItemRepository pizzaRepository;

    @GetMapping("/menu")
    public String menu(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category,
                       HttpSession session) {

        Pageable pageable = PageRequest.of(page, 15);
        Page<MenuItem> pizzaPage;

        if (category != null && !category.isEmpty()) {
            pizzaPage = pizzaRepository.findByCategoryAndAvailable(category, true, pageable);
            model.addAttribute("category", category);
        }
        else if (keyword != null && !keyword.isEmpty()) {
            pizzaPage = pizzaRepository.searchActivePizzas(keyword, pageable);
            model.addAttribute("keyword", keyword);
        }
        else {
            pizzaPage = pizzaRepository.findByAvailable(true, pageable);
        }

        // 🟢 No null check required here anymore for primitive booleans
        // The 'veg' field will default to true/false based on your Entity initialization

        model.addAttribute("pizzas", pizzaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pizzaPage.getTotalPages());

        if (session.getAttribute("favorites") == null) {
            session.setAttribute("favorites", new HashSet<String>());
        }

        return "menu";
    }
}