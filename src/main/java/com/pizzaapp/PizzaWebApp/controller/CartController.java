package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.*;
import com.pizzaapp.PizzaWebApp.model.Cart;
import com.pizzaapp.PizzaWebApp.model.CartItem;
import com.pizzaapp.PizzaWebApp.repository.*;
import com.pizzaapp.PizzaWebApp.service.CartService;
import com.pizzaapp.PizzaWebApp.service.EmailService;
import com.pizzaapp.PizzaWebApp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    @Autowired private CartService cartService;
    @Autowired private UserService userService;
    @Autowired private PendingOrderRepository pendingOrderRepository;


    @GetMapping
    public String viewCart(Model model, Principal principal) {
        String email = principal.getName();

        Cart cart = cartService.getCart(email);

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

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable("id") String id,
                            @RequestParam(value = "selectedSize", required = false) String selectedSize,
                            @RequestParam(value = "note", defaultValue = "") String note,
                            HttpServletRequest request, Principal principal) {

        String email = principal.getName();

        Cart cart =
                cartService.getCart(email);
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

        //session.setAttribute("cart", cart);
        cartService.saveCart(
                email,
                cart
        );

        // Smart Redirect
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }


    @GetMapping("/increase/{id}")
    public String increaseQuantity(@PathVariable String id,Principal principal) {
        String email=principal.getName();
        Cart cart = cartService.getCart(email);
        if (cart != null) {
            for (CartItem item : cart.getItems()) {
                if (item.getMenuItem().getId().equals(id)) {
                    item.setQuantity(item.getQuantity() + 1);
                    break;
                }
            }
            updateCartDiscount(cart);
            cartService.saveCart(email,cart);
        }
        // 🟢 FIX: Redirect back to the main cart page
        return "redirect:/cart";
    }


    @GetMapping("/decrease/{id}")
    public String decreaseQuantity(@PathVariable String id, Principal principal) {
        String email=principal.getName();
        Cart cart = cartService.getCart(email);
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
            cartService.saveCart(email,cart);
        }
        // 🟢 FIX: Redirect back to the main cart page
        return "redirect:/cart";
    }


    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable String id, Principal principal) {
        String email=principal.getName();
        Cart cart = cartService.getCart(email);
        if (cart != null) {
            cart.removeItem(id);
            // Reset coupon if cart is empty
            if(cart.getItems().isEmpty()) {
                cart.setDiscountAmount(0.0);
                cart.setAppliedCouponCode(null);
            }
            cartService.saveCart(email,cart);
        }
        return "redirect:/cart";
    }

    //apply cuopon
    @PostMapping("/apply-coupon")
    public String applyCoupon(@RequestParam String code, Principal principal) {
        String email = principal.getName();
        Cart cart = cartService.getCart(email);
        String cleanCode = code.trim().toUpperCase();
        Coupon coupon = couponRepository.findByCode(cleanCode).orElse(null);
        if(coupon != null && coupon.isActive()){
            double total = cart.getTotalPrice();
            double discount = total * (coupon.getDiscountPercent()/100.0);
            cart.setDiscountAmount(discount);
            cart.setAppliedCouponCode(cleanCode);
            cartService.saveCart(email, cart);
            return "redirect:/cart?coupon=success";
        }
        cart.setDiscountAmount(0.0);
        cart.setAppliedCouponCode(null);
        cartService.saveCart(email, cart);
        return "redirect:/cart?coupon=invalid";
    }


    @GetMapping("/checkout")
    public String checkout( Model model, Principal principal) {
        StoreSettings settings = settingsRepo.findById("global_config").orElse(new StoreSettings());
        if (!settings.isStoreOpen()) return "redirect:/cart?error=StoreIsClosed";
        if (principal == null) return "redirect:/login";

        String email=principal.getName();
        Cart cart = cartService.getCart(email);
        if (cart == null || cart.getItems().isEmpty()) return "redirect:/menu";

        model.addAttribute("email", principal.getName());
        model.addAttribute("subtotal", cart.getGrandTotal());
        model.addAttribute("serviceCharge", cart.getGrandTotal() * 0.05);
        // Add 5% Service Charge to Final Total
        model.addAttribute("grandTotal", cart.getGrandTotal() * 1.05);

        return "checkout";
    }


    @PostMapping("/pay")
    public String processPayment(@RequestParam String name, @RequestParam String address,
                                 @RequestParam String phone, @RequestParam String paymentMethod,Principal principal) {
        String email=principal.getName();
        Cart cart = cartService.getCart(email);
        if (cart.getItems().isEmpty()) return "redirect:/menu";

        double grandTotal = cart.getGrandTotal() * 1.05; // Including Service Charge

        // Save Temp Data
        PendingOrder pending = pendingOrderRepository.findByUserEmail(email).orElse(new PendingOrder());

        pending.setUserEmail(email);
        pending.setCustomerName(name);
        pending.setAddress(address + " | Phone: " + phone);
        pending.setGrandTotal(grandTotal);

        pendingOrderRepository.save(pending);

        if ("UPI".equals(paymentMethod)) {
            return "redirect:/payment?amount=" + grandTotal + "&method=upi";
        } else {
            return "redirect:/payment?amount=" + grandTotal;
        }
    }


    @GetMapping("/pay-final")
    public String finalizeOrder(Principal principal) {
        String email=principal.getName();
        Cart cart = cartService.getCart(email);
        PendingOrder pendingOrder = pendingOrderRepository.findByUserEmail(email).orElse(null);

        if(pendingOrder == null){
            return "redirect:/menu";
        }

        String name = pendingOrder.getCustomerName();

        String address = pendingOrder.getAddress();

        Double grandTotal = pendingOrder.getGrandTotal();

        if (cart.getItems().isEmpty()) return "redirect:/menu";

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

        // Clear cart
        cart.clear();
        cartService.saveCart(email, cart);
        pendingOrderRepository.delete(pendingOrder);

        return "redirect:/cart/success";
    }


    @GetMapping("/success")
    public String showSuccessPage(Model model) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userService.findByEmailUser(email);

        model.addAttribute("userName", user.getName());
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