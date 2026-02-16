package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.Order;
import com.pizzaapp.PizzaWebApp.repository.OrderRepository;
import com.pizzaapp.PizzaWebApp.service.EmailService;
import com.pizzaapp.PizzaWebApp.service.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.security.Principal;

@Controller
//@RequestMapping("/orders")
public class OrderController {


    @Autowired
    PdfService pdfService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/orders")
    public String viewOrders(Model model, Principal principal) { // 🟢 Add Principal

        if (principal == null) {
            return "redirect:/login";
        }

        String userEmail = principal.getName(); // 🟢 GET EMAIL FROM SECURITY

        // Fetch orders
        List<Order> orders = orderRepository.findByEmailOrderByOrderDateDesc(userEmail);

        model.addAttribute("orders", orders);
        model.addAttribute("userEmail", userEmail);
        return "orders";
    }

    // 2. TRACK ORDER
    @GetMapping("/order/track")
    public String trackOrder(@RequestParam String orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        model.addAttribute("order", order);
        return "track-order"; // Ensure you have track-order.html
    }

    // 3. RATE ORDER FORM
    @GetMapping("/order/rate")
    public String rateOrder(@RequestParam String orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        model.addAttribute("order", order);
        return "rate-order"; // Ensure you have rate-order.html
    }

    // Inside OrderController.java
    @PostMapping("/order/submit-rating")
    public String submitRating(@RequestParam String orderId,
                               @RequestParam int rating,
                               @RequestParam String feedback) {
        Order order = orderRepository.findById(orderId).orElse(null);

        // 🟢 CHECK: Only save if the order hasn't been rated yet (rating == 0)
        if (order != null && order.getRating() == 0) {
            order.setRating(rating);
            order.setFeedback(feedback);
            orderRepository.save(order);

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    emailService.sendFeedbackToAdmin(order);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        return "redirect:/orders";
    }

    // Do the same for Cancel Order if you are clicking that fast too:
    @PostMapping("/order/cancel")
    public String cancelOrder(@RequestParam String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null && "PLACED".equalsIgnoreCase(order.getStatus())) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);

            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Wait 2 seconds
                    emailService.sendStatusUpdate(order.getEmail(), order.getId(), "CANCELLED");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        return "redirect:/orders";
    }

    @GetMapping("/order/receipt")
    public String viewReceipt(@RequestParam String orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return "redirect:/orders";
        }
        model.addAttribute("order", order);
        return "receipt"; // Maps to receipt.html
    }


    @GetMapping("/orders/download/{id}")
    public void downloadInvoice(@PathVariable String id, HttpServletResponse response) throws IOException {
        Order order = orderRepository.findById(id).orElse(null);

        if (order != null) {
            // Set the response headers so the browser knows it's a file download
            response.setContentType("application/pdf");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=PizzaReceipt_" + id + ".pdf";
            response.setHeader(headerKey, headerValue);

            // Call the service to stream the PDF directly to the response
            pdfService.generateReceipt(order, response);
        }
    }
    }
