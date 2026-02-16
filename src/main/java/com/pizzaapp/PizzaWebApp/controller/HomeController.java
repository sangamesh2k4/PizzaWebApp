package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import com.pizzaapp.PizzaWebApp.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private MenuItemRepository pizzaRepository;

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword) {

        // --- SECTION 1: BESTSELLERS (The Top 3 Slider) ---
        // Fetches exactly 3 active items from the "Pizza" category
        Pageable topThree = PageRequest.of(0, 3);
        List<MenuItem> bestsellers = pizzaRepository.findByCategoryAndAvailable("Pizza", true, topThree).getContent();
        model.addAttribute("bestsellers", bestsellers);

        // --- SECTION 2: MAIN MENU GRID (Paginated & Searchable) ---
        // 12 items per page for a dense, professional grid layout
        Pageable pageable = PageRequest.of(page, 12);
        Page<MenuItem> pizzaPage;

        if (keyword != null && !keyword.isEmpty()) {
            // Logic: Search by name/desc while ensuring available = true
            pizzaPage = pizzaRepository.searchActivePizzas(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            // Default: Show all active menu items
            pizzaPage = pizzaRepository.findByAvailable(true, pageable);
        }

        // --- SECTION 3: SEND TO HTML ---
        // These attributes drive the 60-30-10 UI design we built
        model.addAttribute("pizzas", pizzaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pizzaPage.getTotalPages());

        return "home"; // This renders your home.html
    }

}