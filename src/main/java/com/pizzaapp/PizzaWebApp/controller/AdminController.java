package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.entity.*;
import com.pizzaapp.PizzaWebApp.repository.*;
import com.pizzaapp.PizzaWebApp.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.mbeans.SparseUserDatabaseMBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired SettingsRepository settingsRepo;

    @GetMapping("/")
        public String adminRoot(){
        return "redirect:/admin/dashboard";
    }

    // 1. DASHBOARD HOME (Stats & Overview)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Calculate Stats
        long totalUsers = userRepository.count();
        long totalItems = menuItemRepository.count();
        long totalOrders = orderRepository.count();

        // Calculate Revenue (Sum of all orders)
        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        List<Order> allOrders = orderRepository.findAll();
        long cooking = allOrders.stream().filter(o -> "COOKING".equalsIgnoreCase(o.getStatus())).count();
        long onTheWay = allOrders.stream().filter(o -> o.getStatus().contains("DELIVERY")).count();
        long delivered = allOrders.stream().filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus())).count();
        long placed = allOrders.size() - (cooking + onTheWay + delivered);

        model.addAttribute("cookingCount", cooking);
        model.addAttribute("onTheWayCount", onTheWay);
        model.addAttribute("deliveredCount", delivered);
        model.addAttribute("placedCount", placed);

        return "admin-dashboard"; // Maps to templates/admin/dashboard.html
    }

    // 2. ORDER MANAGEMENT
    @GetMapping("/orders")
    public String orderManager(Model model) {
        // Fetch orders, newest first
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
        model.addAttribute("orders", orders);
        return "admin-orders"; // Maps to templates/admin/orders.html
    }

    @PostMapping("/order/update")
    public String updateStatus(@RequestParam String orderId, @RequestParam String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);

            // 🟢 SEND EMAIL
            new Thread(() -> {
                emailService.sendStatusUpdate(order.getEmail(), order.getId(), status);
            }).start();
        }
        return "redirect:/admin/orders";
    }

    // 3. MENU MANAGEMENT
    @GetMapping("/menu")
    public String menuManager(Model model) {
        model.addAttribute("pizzas", menuItemRepository.findAll());
        model.addAttribute("newPizza", new MenuItem());
        return "admin-menu"; // Maps to templates/admin/menu.html
    }

    // --- ACTIONS (POST/Redirects) ---

    @PostMapping("/menu/add")
    public String addPizza(@ModelAttribute("newPizza") MenuItem menuItem,
                           @RequestParam(required = false) List<String> sizeNames,
                           @RequestParam(required = false) List<Double> sizeValues,
                           @RequestParam(value = "isVeg", required = false) Boolean isVeg) {

        // 1. Set default category if missing
        if (menuItem.getCategory() == null || menuItem.getCategory().isEmpty()) {
            menuItem.setCategory("Pizza");
        }

        // 2. Convert the Lists (from HTML) into the Map (for MongoDB)
        Map<String, Double> sizesMap = new HashMap<>();

        if (sizeNames != null && sizeValues != null) {
            for (int i = 0; i < sizeNames.size(); i++) {
                String name = sizeNames.get(i).trim();
                Double price = sizeValues.get(i);

                // Only add valid entries (ignore empty rows)
                if (!name.isEmpty() && price != null) {
                    sizesMap.put(name, price);
                }
            }
        }
        if (isVeg == null) {
            menuItem.setVeg(false);
        } else {
            menuItem.setVeg(true);
        }
        menuItem.setSizes(sizesMap);
        menuItem.setAvailable(true);

        // 3. Save to DB
        menuItemRepository.save(menuItem);

        return "redirect:/admin/menu";
    }
    // ... inside AdminController class ...

    // 🟢 DISABLE (Hide from Customer Menu)
    @GetMapping("/menu/disable/{id}")
    public String disablePizza(@PathVariable String id) {
        MenuItem item = menuItemRepository.findById(id).orElse(null);
        if (item != null) {
            item.setAvailable(false); // Set to false
            menuItemRepository.save(item);
        }
        return "redirect:/admin/menu";
    }

    // 🟢 ENABLE (Show on Customer Menu)
    @GetMapping("/menu/enable/{id}")
    public String enablePizza(@PathVariable String id) {
        MenuItem item = menuItemRepository.findById(id).orElse(null);
        if (item != null) {
            item.setAvailable(true); // Set to true
            menuItemRepository.save(item);
        }
        return "redirect:/admin/menu";
    }

    // 4. USER MANAGEMENT (New)
    @GetMapping("/users")
    public String userManager(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users"; // Maps to templates/admin/users.html
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    // 5. CONTACT MESSAGES (New)
    @GetMapping("/messages")
    public String messageManager(Model model) {
        // 1. Fetch all messages/reviews from MongoDB
        List<ContactMessage> messages = contactRepository.findAll();
        // 2. Sort them by date in Java if not using a custom query

        messages.sort((m1, m2) -> m2.getDate().compareTo(m1.getDate()));
        // 3. Add to model for the view
        model.addAttribute("messages", messages);
        model.addAttribute("totalMessages", messages.size());

        return "admin-messages";
    }

    @GetMapping("/messages/delete/{id}")
    public String deleteMessage(@PathVariable String id) {
        contactRepository.deleteById(id);
        return "redirect:/admin/messages";
    }

    // 🟢 1. SHOW EDIT FORM
    @GetMapping("/menu/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        MenuItem item = menuItemRepository.findById(id).orElse(null);
        if (item != null) {
            model.addAttribute("pizza", item); // Pre-fill form with existing data
            return "admin-menu-edit"; // We will create this file
        }
        return "redirect:/admin/menu";
    }

    // 🟢 2. PROCESS UPDATE
    @PostMapping("/menu/update")
    public String updatePizza(@ModelAttribute("pizza") MenuItem menuItem) {
        // We save it using the SAME ID, so MongoDB updates it instead of creating a new one
        menuItemRepository.save(menuItem);
        return "redirect:/admin/menu";
    }

    @GetMapping("/admin/analytics")
    public String getAnalytics(Model model) {
        List<Order> orders = orderRepository.findAll();

        // Grouping data for a Pie Chart (Status Distribution)
        long placed = orders.stream().filter(o -> "PLACED".equalsIgnoreCase(o.getStatus())).count();
        long cooking = orders.stream().filter(o -> "COOKING".equalsIgnoreCase(o.getStatus())).count();
        long delivered = orders.stream().filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus())).count();

        model.addAttribute("placedCount", placed);
        model.addAttribute("cookingCount", cooking);
        model.addAttribute("deliveredCount", delivered);

        // Total Revenue for a Stat Card
        double totalRevenue = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        model.addAttribute("totalRevenue", totalRevenue);

        return "admin-analytics";
    }

    @GetMapping("/ratings")
    public String ratingsMonitor(Model model) {
        // 1. Fetch all orders from MongoDB
        List<Order> allOrders = orderRepository.findAll();

        // 2. Filter for orders that have a rating and sort by date
        List<Order> ratedOrders = allOrders.stream()
                .filter(o -> o.getRating() > 0)
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .toList();

        // 3. Calculate an average for a quick-view stat
        double average = ratedOrders.stream()
                .mapToInt(Order::getRating)
                .average()
                .orElse(0.0);

        model.addAttribute("ratedOrders", ratedOrders);
        model.addAttribute("averageRating", average);

        return "admin-ratings"; // Points to templates/admin-ratings.html
    }

    @PostMapping("/toggle-store")
    public String toggleStore() {
        // Try to get existing settings, or create a fresh object if none exists
        StoreSettings settings = settingsRepo.findById("global_config")
                .orElse(new StoreSettings());

        // Ensure the ID is set correctly
        settings.setId("global_config");

        // Flip the current status
        settings.setStoreOpen(!settings.isStoreOpen());

        // Save it
        settingsRepo.save(settings);

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/fix-data")
    @ResponseBody // This makes it return text, not a template
    public String fixVegNonVegData() {
        List<MenuItem> allItems = menuItemRepository.findAll();
        int updatedCount = 0;

        for (MenuItem item : allItems) {
            String nameLower = item.getName().toLowerCase();
            String descLower = item.getDescription().toLowerCase();

            // 1. Identify Non-Veg Keywords
            if (nameLower.contains("chicken") || nameLower.contains("pepperoni") ||
                    nameLower.contains("meat") || nameLower.contains("non-veg") ||
                    descLower.contains("chicken")) {

                item.setVeg(false); // Mark as Non-Veg
            }
            // 2. Everything else defaults to Veg (Safe bet for Paneer/Cheese/Veg)
            else {
                item.setVeg(true);
            }

            menuItemRepository.save(item);
            updatedCount++;
        }

        return "Success! Scanned and updated " + updatedCount + " items.";
    }
}