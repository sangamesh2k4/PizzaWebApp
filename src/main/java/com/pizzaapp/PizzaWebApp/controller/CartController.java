package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.Coupon;
import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import com.pizzaapp.PizzaWebApp.entity.Order;
import com.pizzaapp.PizzaWebApp.entity.StoreSettings;
import com.pizzaapp.PizzaWebApp.model.Cart;
import com.pizzaapp.PizzaWebApp.model.CartItem;
import com.pizzaapp.PizzaWebApp.repository.CouponRepository;
import com.pizzaapp.PizzaWebApp.repository.MenuItemRepository;
import com.pizzaapp.PizzaWebApp.repository.OrderRepository;
import com.pizzaapp.PizzaWebApp.repository.SettingsRepository;
import com.pizzaapp.PizzaWebApp.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private EmailService emailService;
    @Autowired private SettingsRepository settingsRepo;
    @Autowired private CouponRepository couponRepository;

    // ==========================================
    // 1. VIEW CART (With Upselling & Store Status)
    // ==========================================
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        // Add Cart Data
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("total", cart.getTotalPrice());
        model.addAttribute("discount", cart.getDiscountAmount());
        model.addAttribute("couponCode", cart.getAppliedCouponCode());
        model.addAttribute("finalTotal", cart.getGrandTotal());

        // Check Store Status (Global Config)
        StoreSettings settings = settingsRepo.findById("global_config").orElse(new StoreSettings());
        model.addAttribute("isStoreOpen", settings.isStoreOpen());

        // 🟢 RESTORED UPSELLING LOGIC
        List<MenuItem> allItems = menuItemRepository.findAll();
        Collections.shuffle(allItems);
        List<MenuItem> recommended = allItems.stream().limit(3).collect(Collectors.toList());
        model.addAttribute("recommended", recommended);

        return "cart";
    }

    // ==========================================
    // 2. ADD TO CART (FIXED: Supports Sizes & Notes)
    // ==========================================
    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable("id") String id,
                            @RequestParam(value = "selectedSize", required = false) String selectedSize,
                            @RequestParam(value = "note", defaultValue = "") String note,
                            HttpSession session,
                            HttpServletRequest request) {

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) cart = new Cart();

        MenuItem dbItem = menuItemRepository.findById(id).orElse(null);

        if (dbItem != null) {
            // 1. Handle Size Defaults
            String size = (selectedSize == null || selectedSize.isEmpty()) ? "Standard" : selectedSize;

            // 2. Calculate Price (DB vs Auto-Calc)
            Double finalPrice = 0.0;
            if (dbItem.getSizes() != null && dbItem.getSizes().containsKey(size)) {
                finalPrice = dbItem.getSizes().get(size);
            } else {
                finalPrice = calculateDefaultPrice(dbItem, size);
            }

            // 3. Create "Virtual" Cart Item (The Crash Fix)
            MenuItem cartEntry = new MenuItem();
            // Unique ID per size (e.g., pizza123_Medium) so they don't overwrite each other
            cartEntry.setId(dbItem.getId() + "_" + size);

            String displayName = dbItem.getName() + " (" + size + ")";
            if (!note.isEmpty()) displayName += " [" + note + "]";
            cartEntry.setName(displayName);

            cartEntry.setImageUrl(dbItem.getImageUrl());
            cartEntry.setPrice(finalPrice);

            // 4. Add to Cart
            cart.addCartItem(new CartItem(cartEntry, 1));
        }

        session.setAttribute("cart", cart);

        // Smart Redirect
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    // ==========================================
    // 3. INCREASE QUANTITY (FIXED: Full Page Reload)
    // ==========================================
    @GetMapping("/increase/{id}")
    public String increaseQuantity(@PathVariable String id, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            for (CartItem item : cart.getItems()) {
                if (item.getMenuItem().getId().equals(id)) {
                    item.setQuantity(item.getQuantity() + 1);
                    break;
                }
            }
            updateCartDiscount(cart);
            session.setAttribute("cart", cart);
        }
        // 🟢 FIX: Redirect back to the main cart page
        return "redirect:/cart";
    }

    // ==========================================
    // 4. DECREASE QUANTITY (FIXED: Full Page Reload)
    // ==========================================
    @GetMapping("/decrease/{id}")
    public String decreaseQuantity(@PathVariable String id, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cart.getItems().removeIf(item -> {
                if (item.getMenuItem().getId().equals(id)) {
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        return false;
                    }
                    return true; // Remove if quantity hits 0
                }
                return false;
            });
            updateCartDiscount(cart);
            session.setAttribute("cart", cart);
        }
        // 🟢 FIX: Redirect back to the main cart page
        return "redirect:/cart";
    }

    // ==========================================
    // 5. REMOVE ITEM
    // ==========================================
    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable String id, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cart.removeItem(id);
            // Reset coupon if cart is empty
            if(cart.getItems().isEmpty()) {
                cart.setDiscountAmount(0.0);
                cart.setAppliedCouponCode(null);
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    // ==========================================
    // 6. APPLY COUPON
    // ==========================================
    @PostMapping("/apply-coupon")
    public String applyCoupon(@RequestParam String code, HttpSession session, RedirectAttributes redirectAttributes) {
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart != null) {
            String cleanCode = code.trim().toUpperCase();
            Coupon coupon = couponRepository.findByCode(cleanCode).orElse(null);

            if (coupon != null && coupon.isActive()) {
                double total = cart.getTotalPrice();
                double discount = total * (coupon.getDiscountPercent() / 100.0);
                cart.setDiscountAmount(discount);
                cart.setAppliedCouponCode(cleanCode);
                redirectAttributes.addFlashAttribute("successMsg", "Coupon '" + cleanCode + "' Applied! " + coupon.getDiscountPercent() + "% OFF");
            } else {
                cart.setDiscountAmount(0.0);
                cart.setAppliedCouponCode(null);
                redirectAttributes.addFlashAttribute("errorMsg", "Invalid or Expired Coupon");
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    // ==========================================
    // 7. CHECKOUT PAGE
    // ==========================================
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model, Principal principal) {
        StoreSettings settings = settingsRepo.findById("global_config").orElse(new StoreSettings());
        if (!settings.isStoreOpen()) return "redirect:/cart?error=StoreIsClosed";
        if (principal == null) return "redirect:/login";

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) return "redirect:/menu";

        model.addAttribute("email", principal.getName());
        model.addAttribute("subtotal", cart.getGrandTotal());
        model.addAttribute("serviceCharge", cart.getGrandTotal() * 0.05);
        // Add 5% Service Charge to Final Total
        model.addAttribute("grandTotal", cart.getGrandTotal() * 1.05);

        return "checkout";
    }

    // ==========================================
    // 8. PROCESS PAYMENT (PHASE 1)
    // ==========================================
    @PostMapping("/pay")
    public String processPayment(@RequestParam String name, @RequestParam String address,
                                 @RequestParam String phone, @RequestParam String paymentMethod,
                                 HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) return "redirect:/menu";

        double grandTotal = cart.getGrandTotal() * 1.05; // Including Service Charge

        // Save Temp Data
        session.setAttribute("temp_customer_name", name);
        session.setAttribute("temp_address", address + " | Phone: " + phone);
        session.setAttribute("temp_grand_total", grandTotal);

        if ("UPI".equals(paymentMethod)) {
            return "redirect:/payment?amount=" + grandTotal + "&method=upi";
        } else {
            return "redirect:/payment?amount=" + grandTotal;
        }
    }

    // ==========================================
    // 9. FINALIZE ORDER (PHASE 2 - AFTER BANK)
    // ==========================================
    @GetMapping("/pay-final")
    public String finalizeOrder(HttpSession session, Principal principal) {
        Cart cart = (Cart) session.getAttribute("cart");
        String name = (String) session.getAttribute("temp_customer_name");
        String address = (String) session.getAttribute("temp_address");
        Double grandTotal = (Double) session.getAttribute("temp_grand_total");

        if (cart == null || name == null) return "redirect:/menu";

        Order order = new Order();
        order.setCustomerName(name);
        order.setEmail(principal.getName());
        order.setAddress(address);
        order.setOrderDate(new Date());
        order.setStatus("Paid & Placed");
        order.setTotalAmount(grandTotal);

        List<String> itemNames = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            // Name includes size/note because we set it in addToCart
            String entry = item.getMenuItem().getName() + " | Qty: " + item.getQuantity() + " | ₹" + item.getSubtotal();
            itemNames.add(entry);
        }

        if(cart.getAppliedCouponCode() != null) {
            itemNames.add("--------------------------------");
            itemNames.add("🎟 COUPON: " + cart.getAppliedCouponCode());
            itemNames.add("💰 DISCOUNT: -₹" + cart.getDiscountAmount());
        }

        order.setItems(itemNames);
        orderRepository.save(order);

        // Send Email Async
        new Thread(() -> {
            try {
                emailService.sendOrderConfirmation(order.getEmail(), order.getCustomerName(), order.getId(), order.getTotalAmount(), order.getItems());
            } catch (Exception e) { e.printStackTrace(); }
        }).start();

        // Clear Session
        session.removeAttribute("cart");
        session.removeAttribute("temp_customer_name");
        session.removeAttribute("temp_address");
        session.removeAttribute("temp_grand_total");

        return "redirect:/cart/success";
    }

    // ==========================================
    // 10. SHOW SUCCESS PAGE
    // ==========================================
    @GetMapping("/success")
    public String showSuccessPage() {
        return "ordersuccess";
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================
    private void updateCartDiscount(Cart cart) {
        if (cart.getAppliedCouponCode() != null) {
            couponRepository.findByCode(cart.getAppliedCouponCode()).ifPresent(coupon -> {
                double newDiscount = cart.getTotalPrice() * (coupon.getDiscountPercent() / 100.0);
                cart.setDiscountAmount(newDiscount);
            });
        }
    }

    private Double calculateDefaultPrice(MenuItem item, String targetSize) {
        // 1. Determine Base Price
        // If "sizes" map exists, grab the lowest price. Otherwise, use the flat price.
        Double basePrice = item.getPrice();
        if (item.getSizes() != null && !item.getSizes().isEmpty()) {
            basePrice = Collections.min(item.getSizes().values());
        }

        // 2. Normalize Category
        String category = item.getCategory() != null ? item.getCategory().toLowerCase() : "pizza";
        double increment = 0.0;

        // 3. Calculate Increment based on Size
        if (category.contains("pizza")) {
            if ("Medium".equalsIgnoreCase(targetSize)) increment = 70.0;
            else if ("Large".equalsIgnoreCase(targetSize)) increment = 130.0;
        }
        else if (category.contains("side") || category.contains("burger")) {
            if ("Medium".equalsIgnoreCase(targetSize)) increment = 50.0;
            else if ("Large".equalsIgnoreCase(targetSize)) increment = 100.0;
        }
        else if (category.contains("drink") || category.contains("beverage")) {
            if ("Medium".equalsIgnoreCase(targetSize)) increment = 20.0;
            else if ("Large".equalsIgnoreCase(targetSize)) increment = 40.0;
        }

        return basePrice + increment;
    }
}