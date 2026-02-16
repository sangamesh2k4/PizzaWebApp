package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import com.pizzaapp.PizzaWebApp.repository.MenuItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired private MenuItemRepository menuItemRepository;

    // 1. View Favorites Page
    @GetMapping
    public String showFavorites(HttpSession session, Model model) {
        Set<String> favIds = (Set<String>) session.getAttribute("favorites");
        if (favIds == null) favIds = new HashSet<>();

        // Fetch only the items Sangu has liked
        List<MenuItem> favoriteItems = (List<MenuItem>) menuItemRepository.findAllById(favIds);

        model.addAttribute("pizzas", favoriteItems);
        model.addAttribute("isFavoritesPage", true);
        model.addAttribute("isStoreOpen", true); // Pass store status if needed

        return "menu"; // Reuse menu.html to show the cards!
    }

    // 2. Toggle Favorite (AJAX Friendly)
    @PostMapping("/toggle/{id}")
    @ResponseBody
    public Map<String, Object> toggleFavorite(@PathVariable String id, HttpSession session) {
        Set<String> favorites = (Set<String>) session.getAttribute("favorites");
        if (favorites == null) favorites = new HashSet<>();

        boolean added;
        if (favorites.contains(id)) {
            favorites.remove(id);
            added = false;
        } else {
            favorites.add(id);
            added = true;
        }

        session.setAttribute("favorites", favorites);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isFavorite", added);
        return response;
    }
}