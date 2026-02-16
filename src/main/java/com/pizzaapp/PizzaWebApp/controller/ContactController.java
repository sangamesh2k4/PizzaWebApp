package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.ContactMessage;
import com.pizzaapp.PizzaWebApp.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    // 1. Show the Contact Page
    @GetMapping("/contact")
    public String showContactPage() {
        return "contact"; // Matches contact.html
    }

    // 2. Handle the Form Submission
    @PostMapping("/submit-contact")
    public String submitContact(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String subject,
                                @RequestParam String message,
                                Model model) {

        // Create and Save the Message
        ContactMessage msg = new ContactMessage();
        msg.setName(name);
        msg.setEmail(email);
        msg.setSubject(subject);
        msg.setMessage(message);

        contactRepository.save(msg);

        // Add a success message to display on the page
        model.addAttribute("success", "Thank you! We have received your message.");

        return "contact"; // Stay on the same page but show success message
    }
}