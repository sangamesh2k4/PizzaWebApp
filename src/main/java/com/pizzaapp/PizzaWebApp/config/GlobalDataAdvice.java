package com.pizzaapp.PizzaWebApp.config;

import com.pizzaapp.PizzaWebApp.entity.StoreSettings;
import com.pizzaapp.PizzaWebApp.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalDataAdvice {

    @Autowired
    private SettingsRepository settingsRepo;

    @ModelAttribute
    public void addStoreStatus(Model model) {
        StoreSettings settings = settingsRepo.findById("global_config").orElse(new StoreSettings());
        model.addAttribute("isStoreOpen", settings.isStoreOpen());
    }
}
