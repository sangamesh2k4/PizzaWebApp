package com.pizzaapp.PizzaWebApp.model;

import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Data; // Keep this if you have it
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    private MenuItem menuItem;
    private int quantity;


    // 🟢 1. Explicit Setter (Fixes 'Cannot resolve method setQuantity')
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // 🟢 2. Explicit Getter
    public int getQuantity() {
        return quantity;
    }

    // 🟢 3. Getter/Setter for MenuItem (Just to be safe)
    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    // 🟢 4. Subtotal Calculation
    public double getSubtotal() {
        if (menuItem != null) {
            return menuItem.getPrice() * quantity;
        }
        return 0.0;
    }
}