package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import com.pizzaapp.PizzaWebApp.repository.MenuItemRepository;
import com.pizzaapp.PizzaWebApp.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.event.PaintEvent;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private FavoriteService favoriteService;

    // 1. View Favorites Page
    @GetMapping
    public String showFavorites(Principal principal, Model model) {
        String email =
                principal.getName();Set<String>
                favIds = favoriteService.getFavorites(email);
        if (favIds == null) favIds = new HashSet<>();

        // Fetch only the items Sangu has liked
        List<MenuItem> favoriteItems = (List<MenuItem>) menuItemRepository.findAllById(favIds);

        model.addAttribute("pizzas", favoriteItems);
        model.addAttribute("isFavoritesPage", true);
        model.addAttribute("isStoreOpen", true); // Pass store status if needed

        return "favorites";
    }


    // 2. Toggle Favorite (AJAX Friendly)
    @PostMapping("/toggle/{id}")
    @ResponseBody
    public Map<String, Object> toggleFavorite(@PathVariable String id,Principal principal) {
        String email = principal.getName();

        Set<String> favorites =favoriteService.getFavorites(email);
        if (favorites == null) favorites = new HashSet<>();

        boolean added;
        if (favorites.contains(id)) {
            favorites.remove(id);
            added = false;
        } else {
            favorites.add(id);
            added = true;
        }

        favoriteService.saveFavorites(email, favorites);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isFavorite", added);
        return response;
    }
}