package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.Coupon;
import com.pizzaapp.PizzaWebApp.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/coupons")
public class AdminCouponController {

    @Autowired
    private CouponRepository couponRepository;

    // 1. List all coupons
    @GetMapping
    public String listCoupons(Model model) {
        model.addAttribute("coupons", couponRepository.findAll());
        return "admin-coupons"; // We will create this HTML next
    }

    // 2. Show "Add New Coupon" Form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        return "admin-coupon-form"; // We will create this HTML too
    }

    // 3. Save the Coupon
    @PostMapping("/save")
    public String saveCoupon(@ModelAttribute Coupon coupon) {
        // Force uppercase code (e.g., "save20" -> "SAVE20")
        coupon.setCode(coupon.getCode().toUpperCase());
        coupon.setActive(true); // Auto-activate
        couponRepository.save(coupon);
        return "redirect:/admin/coupons";
    }

    // 4. Delete a Coupon
    @GetMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable String id) {
        couponRepository.deleteById(id);
        return "redirect:/admin/coupons";
    }
}