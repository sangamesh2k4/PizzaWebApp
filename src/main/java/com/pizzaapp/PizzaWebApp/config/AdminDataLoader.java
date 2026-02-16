package com.pizzaapp.PizzaWebApp.config;

import com.pizzaapp.PizzaWebApp.entity.Category;
import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import com.pizzaapp.PizzaWebApp.entity.User;
import com.pizzaapp.PizzaWebApp.repository.CategoryRepository;
import com.pizzaapp.PizzaWebApp.repository.MenuItemRepository;
import com.pizzaapp.PizzaWebApp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AdminDataLoader implements CommandLineRunner {

    // 1. Dependency Injection
    private final MenuItemRepository pizzaRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataLoader(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           CategoryRepository categoryRepository,
                           MenuItemRepository pizzaRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.pizzaRepository = pizzaRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // --- 1. Create Default Admin ---
        String targetEmail = "admin@gmail.com";
        if (userRepository.findByEmail(targetEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(targetEmail);
            admin.setName("Admin User");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            userRepository.save(admin);

            System.out.println("-------------------------------------------");
            System.out.println("✅ DEFAULT ADMIN CREATED");
            System.out.println("Email: " + targetEmail);
            System.out.println("Password: admin123");
            System.out.println("-------------------------------------------");
        }

        // --- 2. Create Categories ---
        if (categoryRepository.count() == 0) {
            List<Category> categories = Arrays.asList(
                    new Category("Pizza"), // Fixed Name to match Menu Items
                    new Category("Side"),
                    new Category("Drink")
            );
            categoryRepository.saveAll(categories);
            System.out.println("📂 Categories added to MongoDB.");
        }

        // --- 3. Create Menu Items ---
        // 🟢 FIX: Run if database is empty (count == 0), not specific number like 21
        if (pizzaRepository.count() == 0) {

            // --- PIZZAS ---
            MenuItem p9 = new MenuItem();
            p9.setName("Veg Supreme");
            p9.setDescription("Loaded with premium vegetables and cheese");
            p9.setPrice(309.0);
            p9.setAvailable(true);
            p9.setCategory("Pizza");
            p9.setImageUrl("/images/veg-supreme.jpg");

            MenuItem p10 = new MenuItem();
            p10.setName("Mexican Green Wave");
            p10.setDescription("Spicy Mexican herbs with crunchy veggies");
            p10.setPrice(269.0);
            p10.setAvailable(true);
            p10.setCategory("Pizza");
            p10.setImageUrl("/images/mexican-green-wave.jpg");

            MenuItem p11 = new MenuItem();
            p11.setName("Tandoori Paneer Pizza");
            p11.setDescription("Smoky tandoori paneer with onion and capsicum");
            p11.setPrice(299.0);
            p11.setAvailable(true);
            p11.setCategory("Pizza");
            p11.setImageUrl("/images/tandoori-paneer.jpg");

            MenuItem p12 = new MenuItem();
            p12.setName("Chicken Tikka Pizza");
            p12.setDescription("Spicy chicken tikka chunks on cheesy base");
            p12.setPrice(339.0);
            p12.setAvailable(true);
            p12.setCategory("Pizza");
            p12.setImageUrl("/images/chicken-tikka.jpg");

            MenuItem p13 = new MenuItem();
            p13.setName("Peri Peri Chicken Pizza");
            p13.setDescription("Hot peri peri chicken with fiery seasoning");
            p13.setPrice(349.0);
            p13.setAvailable(true);
            p13.setCategory("Pizza");
            p13.setImageUrl("/images/peri-peri-chicken.jpg");

            MenuItem p14 = new MenuItem();
            p14.setName("Cheese Overload");
            p14.setDescription("Multiple layers of rich melted cheese");
            p14.setPrice(329.0);
            p14.setAvailable(true);
            p14.setCategory("Pizza");
            p14.setImageUrl("/images/cheese-overload.jpg");

            MenuItem p15 = new MenuItem();
            p15.setName("Chicken Pepperoni");
            p15.setDescription("Spicy chicken pepperoni with mozzarella");
            p15.setPrice(359.0);
            p15.setAvailable(true);
            p15.setCategory("Pizza");
            p15.setImageUrl("/images/chicken-pepperoni.jpg");

            MenuItem p16 = new MenuItem();
            p16.setName("Classic Veg Pizza");
            p16.setDescription("Simple veg pizza with onion and capsicum");
            p16.setPrice(219.0);
            p16.setAvailable(true);
            p16.setCategory("Pizza");
            p16.setImageUrl("/images/classic-veg.jpg");

            MenuItem p17 = new MenuItem();
            p17.setName("Corn & Cheese Pizza");
            p17.setDescription("Sweet corn with extra cheese");
            p17.setPrice(239.0);
            p17.setAvailable(true);
            p17.setCategory("Pizza");
            p17.setImageUrl("/images/corn-cheese.jpg");

            MenuItem p18 = new MenuItem();
            p18.setName("Mushroom Delight");
            p18.setDescription("Fresh mushrooms with mozzarella cheese");
            p18.setPrice(249.0);
            p18.setAvailable(true);
            p18.setCategory("Pizza");
            p18.setImageUrl("/images/mushroom-delight.jpg");

            MenuItem p19 = new MenuItem();
            p19.setName("Veggie Feast");
            p19.setDescription("Assorted vegetables with herbs");
            p19.setPrice(289.0);
            p19.setAvailable(true);
            p19.setCategory("Pizza");
            p19.setImageUrl("/images/veggie-feast.jpg");

            MenuItem p20 = new MenuItem();
            p20.setName("Paneer Butter Masala Pizza");
            p20.setDescription("Rich paneer butter masala topping");
            p20.setPrice(329.0);
            p20.setAvailable(true);
            p20.setCategory("Pizza");
            p20.setImageUrl("/images/paneer-butter-masala.jpg");

            MenuItem p21 = new MenuItem();
            p21.setName("Chicken Sausage Pizza");
            p21.setDescription("Juicy chicken sausage with cheese");
            p21.setPrice(319.0);
            p21.setAvailable(true);
            p21.setCategory("Pizza");
            p21.setImageUrl("/images/chicken-sausage.jpg");

            MenuItem p22 = new MenuItem();
            p22.setName("Garlic Chicken Pizza");
            p22.setDescription("Garlic flavored chicken topping");
            p22.setPrice(339.0);
            p22.setAvailable(true);
            p22.setCategory("Pizza");
            p22.setImageUrl("/images/garlic-chicken.jpg");

            MenuItem p23 = new MenuItem();
            p23.setName("Hyderabadi Chicken Pizza");
            p23.setDescription("Spicy Hyderabadi-style chicken");
            p23.setPrice(359.0);
            p23.setAvailable(true);
            p23.setCategory("Pizza");
            p23.setImageUrl("/images/hyderabadi-chicken.jpg");

            MenuItem p24 = new MenuItem();
            p24.setName("Double Cheese Margherita");
            p24.setDescription("Classic margherita with double cheese");
            p24.setPrice(279.0);
            p24.setAvailable(true);
            p24.setCategory("Pizza");
            p24.setImageUrl("/images/double-cheese.jpg");

            MenuItem p25 = new MenuItem();
            p25.setName("Veg Keema Pizza");
            p25.setDescription("Plant-based keema with Indian spices");
            p25.setPrice(299.0);
            p25.setAvailable(true);
            p25.setCategory("Pizza");
            p25.setImageUrl("/images/veg-keema.jpg");

            MenuItem p26 = new MenuItem();
            p26.setName("BBQ Paneer Pizza");
            p26.setDescription("Barbecue paneer with smoky flavor");
            p26.setPrice(309.0);
            p26.setAvailable(true);
            p26.setCategory("Pizza");
            p26.setImageUrl("/images/bbq-paneer.jpg");

            MenuItem p27 = new MenuItem();
            p27.setName("Italian Veg Pizza");
            p27.setDescription("Italian herbs with fresh vegetables");
            p27.setPrice(279.0);
            p27.setAvailable(true);
            p27.setCategory("Pizza");
            p27.setImageUrl("/images/italian-veg.jpg");

            MenuItem p28 = new MenuItem();
            p28.setName("Italian Chicken Pizza");
            p28.setDescription("Italian-seasoned chicken with cheese");
            p28.setPrice(339.0);
            p28.setAvailable(true);
            p28.setCategory("Pizza");
            p28.setImageUrl("/images/italian-chicken.jpg");

            MenuItem p29 = new MenuItem();
            p29.setName("Spicy Veg Pizza");
            p29.setDescription("Hot and spicy vegetable toppings");
            p29.setPrice(259.0);
            p29.setAvailable(true);
            p29.setCategory("Pizza");
            p29.setImageUrl("/images/spicy-veggie.jpg");

            MenuItem p30 = new MenuItem();
            p30.setName("Spicy Chicken Pizza");
            p30.setDescription("Extra spicy chicken with fiery sauce");
            p30.setPrice(349.0);
            p30.setAvailable(true);
            p30.setCategory("Pizza");
            p30.setImageUrl("/images/spicy-chicken.jpg");

            MenuItem p31 = new MenuItem();
            p31.setName("Veggie Crunch Pizza");
            p31.setDescription("Crunchy veggies with cheese");
            p31.setPrice(269.0);
            p31.setAvailable(true);
            p31.setCategory("Pizza");
            p31.setImageUrl("/images/veggie-crunch.jpg");

            MenuItem p32 = new MenuItem();
            p32.setName("Chicken Supreme Pizza");
            p32.setDescription("Loaded chicken toppings with herbs");
            p32.setPrice(389.0);
            p32.setAvailable(true);
            p32.setCategory("Pizza");
            p32.setImageUrl("/images/chicken-supreme.jpg");


            // --- SIDES ---
            MenuItem s8 = new MenuItem();
            s8.setName("Potato Wedges");
            s8.setDescription("Crispy potato wedges with seasoning");
            s8.setPrice(139.0);
            s8.setAvailable(true);
            s8.setCategory("Side");
            s8.setImageUrl("/images/potato-wedges.jpg");

            MenuItem s9 = new MenuItem();
            s9.setName("Cheese Balls");
            s9.setDescription("Crispy cheese-filled balls");
            s9.setPrice(169.0);
            s9.setAvailable(true);
            s9.setCategory("Side");
            s9.setImageUrl("/images/cheese-balls.jpg");

            MenuItem s10 = new MenuItem();
            s10.setName("Veg Nuggets");
            s10.setDescription("Crunchy vegetable nuggets");
            s10.setPrice(149.0);
            s10.setAvailable(true);
            s10.setCategory("Side");
            s10.setImageUrl("/images/veg-nuggets.jpg");

            MenuItem s11 = new MenuItem();
            s11.setName("Chicken Nuggets");
            s11.setDescription("Crispy chicken nuggets");
            s11.setPrice(199.0);
            s11.setAvailable(true);
            s11.setCategory("Side");
            s11.setImageUrl("/images/chicken-nuggets.jpg");

            MenuItem s12 = new MenuItem();
            s12.setName("Stuffed Garlic Bread");
            s12.setDescription("Garlic bread stuffed with cheese");
            s12.setPrice(179.0);
            s12.setAvailable(true);
            s12.setCategory("Side");
            s12.setImageUrl("/images/stuffed-garlic-bread.jpg");

            MenuItem s13 = new MenuItem();
            s13.setName("Chicken Strips");
            s13.setDescription("Spicy and crispy chicken strips");
            s13.setPrice(229.0);
            s13.setAvailable(true);
            s13.setCategory("Side");
            s13.setImageUrl("/images/chicken-strips.jpg");

            MenuItem s14 = new MenuItem();
            s14.setName("Veg Spring Rolls");
            s14.setDescription("Crispy veg spring rolls");
            s14.setPrice(159.0);
            s14.setAvailable(true);
            s14.setCategory("Side");
            s14.setImageUrl("/images/veg-spring-rolls.jpg");

            MenuItem s15 = new MenuItem();
            s15.setName("Onion Rings");
            s15.setDescription("Crispy fried onion rings");
            s15.setPrice(139.0);
            s15.setAvailable(true);
            s15.setCategory("Side");
            s15.setImageUrl("/images/onion-rings.jpg");

            MenuItem s16 = new MenuItem();
            s16.setName("Cheesy Fries");
            s16.setDescription("French fries topped with cheese");
            s16.setPrice(159.0);
            s16.setAvailable(true);
            s16.setCategory("Side");
            s16.setImageUrl("/images/cheesy-fries.jpg");

            MenuItem s17 = new MenuItem();
            s17.setName("Veg Cutlet");
            s17.setDescription("Crispy veg cutlets");
            s17.setPrice(129.0);
            s17.setAvailable(true);
            s17.setCategory("Side");
            s17.setImageUrl("/images/veg-cutlet.jpg");

            MenuItem s18 = new MenuItem();
            s18.setName("Chicken Cutlet");
            s18.setDescription("Golden fried chicken cutlets");
            s18.setPrice(189.0);
            s18.setAvailable(true);
            s18.setCategory("Side");
            s18.setImageUrl("/images/chicken-cutlet.jpg");

            MenuItem s19 = new MenuItem();
            s19.setName("Paneer Pakora");
            s19.setDescription("Crispy paneer fritters");
            s19.setPrice(169.0);
            s19.setAvailable(true);
            s19.setCategory("Side");
            s19.setImageUrl("/images/paneer-pakora.jpg");

            MenuItem s20 = new MenuItem();
            s20.setName("Chicken Pakora");
            s20.setDescription("Spicy chicken fritters");
            s20.setPrice(219.0);
            s20.setAvailable(true);
            s20.setCategory("Side");
            s20.setImageUrl("/images/chicken-pakora.jpg");

            MenuItem s21 = new MenuItem();
            s21.setName("Veg Manchurian");
            s21.setDescription("Indo-Chinese veg manchurian");
            s21.setPrice(179.0);
            s21.setAvailable(true);
            s21.setCategory("Side");
            s21.setImageUrl("/images/veg-manchurian.jpg");

            MenuItem s22 = new MenuItem();
            s22.setName("Chicken Manchurian");
            s22.setDescription("Indo-Chinese chicken manchurian");
            s22.setPrice(229.0);
            s22.setAvailable(true);
            s22.setCategory("Side");
            s22.setImageUrl("/images/chicken-manchurian.jpg");

            MenuItem s23 = new MenuItem();
            s23.setName("Cheese Garlic Toast");
            s23.setDescription("Toasted bread with garlic and cheese");
            s23.setPrice(149.0);
            s23.setAvailable(true);
            s23.setCategory("Side");
            s23.setImageUrl("/images/cheese-garlic-toast.jpg");

            MenuItem s24 = new MenuItem();
            s24.setName("Veg Momos");
            s24.setDescription("Steamed veg momos");
            s24.setPrice(159.0);
            s24.setAvailable(true);
            s24.setCategory("Side");
            s24.setImageUrl("/images/veg-momos.jpg");

            MenuItem s25 = new MenuItem();
            s25.setName("Chicken Momos");
            s25.setDescription("Steamed chicken momos");
            s25.setPrice(199.0);
            s25.setAvailable(true);
            s25.setCategory("Side");
            s25.setImageUrl("/images/chicken-momos.jpg");

            MenuItem s26 = new MenuItem();
            s26.setName("Paneer Momos");
            s26.setDescription("Soft momos stuffed with paneer");
            s26.setPrice(179.0);
            s26.setAvailable(true);
            s26.setCategory("Side");
            s26.setImageUrl("/images/paneer-momos.jpg");

            MenuItem s27 = new MenuItem();
            s27.setName("Veg Cheese Sandwich");
            s27.setDescription("Grilled veg sandwich with cheese");
            s27.setPrice(139.0);
            s27.setAvailable(true);
            s27.setCategory("Side");
            s27.setImageUrl("/images/veg-cheese-sandwich.jpg");

            MenuItem s28 = new MenuItem();
            s28.setName("Chicken Cheese Sandwich");
            s28.setDescription("Grilled chicken sandwich with cheese");
            s28.setPrice(189.0);
            s28.setAvailable(true);
            s28.setCategory("Side");
            s28.setImageUrl("/images/chicken-cheese-sandwich.jpg");

            MenuItem s29 = new MenuItem();
            s29.setName("Paneer Roll");
            s29.setDescription("Paneer wrapped in soft roll");
            s29.setPrice(169.0);
            s29.setAvailable(true);
            s29.setCategory("Side");
            s29.setImageUrl("/images/paneer-roll.jpg");

            MenuItem s30 = new MenuItem();
            s30.setName("Chicken Roll");
            s30.setDescription("Chicken wrapped in soft roll");
            s30.setPrice(219.0);
            s30.setAvailable(true);
            s30.setCategory("Side");
            s30.setImageUrl("/images/chicken-roll.jpg");


            // --- DRINKS ---
            MenuItem d7 = new MenuItem();
            d7.setName("Fanta");
            d7.setDescription("Orange flavored cold drink");
            d7.setPrice(49.0);
            d7.setAvailable(true);
            d7.setCategory("Drink");
            d7.setImageUrl("/images/fanta.jpg");

            MenuItem d8 = new MenuItem();
            d8.setName("Thumbs Up");
            d8.setDescription("Strong fizzy cola drink");
            d8.setPrice(49.0);
            d8.setAvailable(true);
            d8.setCategory("Drink");
            d8.setImageUrl("/images/thumbs-up.jpg");

            MenuItem d9 = new MenuItem();
            d9.setName("Iced Tea");
            d9.setDescription("Refreshing lemon iced tea");
            d9.setPrice(79.0);
            d9.setAvailable(true);
            d9.setCategory("Drink");
            d9.setImageUrl("/images/iced-tea.jpg");

            MenuItem d10 = new MenuItem();
            d10.setName("Strawberry Milkshake");
            d10.setDescription("Thick strawberry milkshake");
            d10.setPrice(129.0);
            d10.setAvailable(true);
            d10.setCategory("Drink");
            d10.setImageUrl("/images/strawberry-milkshake.jpg");

            MenuItem d11 = new MenuItem();
            d11.setName("Mango Milkshake");
            d11.setDescription("Fresh mango milkshake");
            d11.setPrice(129.0);
            d11.setAvailable(true);
            d11.setCategory("Drink");
            d11.setImageUrl("/images/mango-milkshake.jpg");

            MenuItem d12 = new MenuItem();
            d12.setName("Chocolate Cold Coffee");
            d12.setDescription("Cold coffee with chocolate flavor");
            d12.setPrice(119.0);
            d12.setAvailable(true);
            d12.setCategory("Drink");
            d12.setImageUrl("/images/chocolate-cold-coffee.jpg");

            MenuItem d13 = new MenuItem();
            d13.setName("Lime Soda");
            d13.setDescription("Fresh lime soda");
            d13.setPrice(59.0);
            d13.setAvailable(true);
            d13.setCategory("Drink");
            d13.setImageUrl("/images/lime-soda.jpg");

            MenuItem d14 = new MenuItem();
            d14.setName("Masala Soda");
            d14.setDescription("Spicy masala soda");
            d14.setPrice(59.0);
            d14.setAvailable(true);
            d14.setCategory("Drink");
            d14.setImageUrl("/images/masala-soda.jpg");

            MenuItem d15 = new MenuItem();
            d15.setName("Orange Juice");
            d15.setDescription("Fresh orange juice");
            d15.setPrice(89.0);
            d15.setAvailable(true);
            d15.setCategory("Drink");
            d15.setImageUrl("/images/orange-juice.jpg");

            MenuItem d16 = new MenuItem();
            d16.setName("Apple Juice");
            d16.setDescription("Chilled apple juice");
            d16.setPrice(89.0);
            d16.setAvailable(true);
            d16.setCategory("Drink");
            d16.setImageUrl("/images/apple-juice.jpg");

            MenuItem d17 = new MenuItem();
            d17.setName("Vanilla Milkshake");
            d17.setDescription("Classic vanilla milkshake");
            d17.setPrice(119.0);
            d17.setAvailable(true);
            d17.setCategory("Drink");
            d17.setImageUrl("/images/vanilla-milkshake.jpg");

            MenuItem d18 = new MenuItem();
            d18.setName("Buttermilk");
            d18.setDescription("Cool and refreshing buttermilk");
            d18.setPrice(39.0);
            d18.setAvailable(true);
            d18.setCategory("Drink");
            d18.setImageUrl("/images/buttermilk.jpg");

            MenuItem d19 = new MenuItem();
            d19.setName("Mint Mojito");
            d19.setDescription("Refreshing mint mojito");
            d19.setPrice(99.0);
            d19.setAvailable(true);
            d19.setCategory("Drink");
            d19.setImageUrl("/images/mint-mojito.jpg");

            MenuItem d20 = new MenuItem();
            d20.setName("Blue Lagoon");
            d20.setDescription("Cool blue lagoon mocktail");
            d20.setPrice(109.0);
            d20.setAvailable(true);
            d20.setCategory("Drink");
            d20.setImageUrl("/images/blue-lagoon.jpg");

            MenuItem d21 = new MenuItem();
            d21.setName("Watermelon Juice");
            d21.setDescription("Fresh watermelon juice");
            d21.setPrice(79.0);
            d21.setAvailable(true);
            d21.setCategory("Drink");
            d21.setImageUrl("/images/watermelon-juice.jpg");

            MenuItem d22 = new MenuItem();
            d22.setName("Pineapple Juice");
            d22.setDescription("Chilled pineapple juice");
            d22.setPrice(89.0);
            d22.setAvailable(true);
            d22.setCategory("Drink");
            d22.setImageUrl("/images/pineapple-juice.jpg");

            MenuItem d23 = new MenuItem();
            d23.setName("Cold Badam Milk");
            d23.setDescription("Chilled badam milk");
            d23.setPrice(69.0);
            d23.setAvailable(true);
            d23.setCategory("Drink");
            d23.setImageUrl("/images/badam-milk.jpg");

            MenuItem d24 = new MenuItem();
            d24.setName("Rose Milk");
            d24.setDescription("Sweet rose-flavored milk");
            d24.setPrice(59.0);
            d24.setAvailable(true);
            d24.setCategory("Drink");
            d24.setImageUrl("/images/rose-milk.jpg");


            List<MenuItem> allMenuItems = Arrays.asList(
                    // Pizzas
                    p9, p10, p11, p12, p13, p14, p15, p16,
                    p17, p18, p19, p20, p21, p22, p23, p24,
                    p25, p26, p27, p28, p29, p30, p31, p32,

                    // Sides
                    s8, s9, s10, s11, s12, s13, s14,
                    s15, s16, s17, s18, s19, s20, s21, s22,
                    s23, s24, s25, s26, s27, s28, s29, s30,

                    // Drinks
                    d7, d8, d9, d10, d11, d12,
                    d13, d14, d15, d16, d17, d18,
                    d19, d20, d21, d22, d23, d24
            );

            // 🟢 CORRECTED: Use pizzaRepository
            pizzaRepository.saveAll(allMenuItems);
            System.out.println("✅ All Menu Items (Pizzas, Sides, Drinks) saved to MongoDB!");
        }
    }
}