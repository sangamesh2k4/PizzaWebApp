package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import com.pizzaapp.PizzaWebApp.repository.MenuItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.pizzaapp.PizzaWebApp.service.PizzaService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final MenuItemRepository pizzaRepository;
    private final PizzaService pizzaService;

    // 🟢 CONSTRUCTOR INJECTION: Spring MUST provide the repository here
    public PageController(MenuItemRepository pizzaRepository, PizzaService pizzaService) {
        this.pizzaRepository = pizzaRepository;
        this.pizzaService=pizzaService;
    }
    // When you open localhost, this will redirect you to login page
    @GetMapping("/welcome")
    public String defaultPage() {
        return "redirect:/home";
    }


    // Displays the registration page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // loads register.html from /templates
    }

    // Displays the home page (after login)
    @GetMapping("/home")
    public String showHomePage(Model model, @RequestParam (defaultValue = "0") int page ,@RequestParam(required =false ) String keyword) {

        Pageable pageable = PageRequest.of(page,12);
        Page<MenuItem> pizzaPage;
        if(keyword!= null && !keyword.isEmpty()){
            pizzaPage=pizzaRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword,keyword,pageable);
            model.addAttribute("keyword",keyword);
        } else{
            pizzaPage=pizzaRepository.findAll(pageable);
        }
        model.addAttribute("pizzas", pizzaPage.getContent());
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",pizzaPage.getTotalPages());
       // List<Pizza> pizzas = pizzaService.getAllPizzas();
        //System.out.println("Home pizzas count = " + pizzas.size());

        //model.addAttribute("pizzas", pizzas);
        return "home";
    }

    // Optional: about/contact pages if needed later
    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

//    @GetMapping("/contact")
//    public String contactPage() {
//        return "contact";
//    }


}
