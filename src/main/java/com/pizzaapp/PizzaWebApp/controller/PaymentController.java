package com.pizzaapp.PizzaWebApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    // 🟢 SINGLE GET METHOD (Handles BOTH Card and UPI)
    // We removed the duplicate method. This one handles everything.
    @GetMapping
    public String showPaymentPage(@RequestParam double amount,
                                  @RequestParam(required = false) String method,
                                  Model model) {

        model.addAttribute("amount", amount);

        // 1. If the user clicked "UPI", show the QR Code page
        if ("upi".equalsIgnoreCase(method)) {
            return "fake-upi-waiting";
        }

        // 2. Otherwise (default), show the Bank OTP page
        return "fake-payment";
    }

    // 🟢 POST METHOD (Handles OTP Verification)
    @PostMapping("/process")
    public String processFakePayment(@RequestParam String otp, RedirectAttributes ra) {
        if ("1234".equals(otp)) {
            // Success: Go back to CartController to save the order
            return "redirect:/cart/pay-final";
        } else {
            // Failure: Go back to payment page with error
            ra.addFlashAttribute("errorMsg", "Invalid OTP! Try 1234");
            // Note: In a real app, we would pass the original amount back here.
            // For now, 0 prevents a crash, but ideally, you'd pass the actual amount.
            return "redirect:/payment?amount=0";
        }
    }
}