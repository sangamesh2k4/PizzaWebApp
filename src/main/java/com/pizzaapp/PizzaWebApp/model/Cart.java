package com.pizzaapp.PizzaWebApp.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Cart {

    private List<CartItem> items = new ArrayList<>();
    private double discountAmount = 0.0;
    private String appliedCouponCode = null;


    public void addCartItem(CartItem newItem) {
        boolean found = false;

        // 1. Check if item exists
        for (CartItem item : items) {
            // Compare IDs to see if it's the same pizza
            if (item.getMenuItem().getId().equals(newItem.getMenuItem().getId())) {
                // If yes, just update quantity (e.g., 1 -> 2)
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) {
            this.items.add(newItem);
        }
    }
    public void removeItem(String itemId) {
        items.removeIf(item -> item.getMenuItem().getId().equals(itemId));
    }


    public int getTotalQuantity() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Clears the cart (useful after checkout is successful).
     */
    public void clear() {
        items.clear();
    }

    public double getTotalPrice() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }


    // 🟢 NEW: Calculate Final Total after Discount
    public double getGrandTotal() {
        return getTotalPrice() - discountAmount;

}

    public int getQuantity(String itemId) {
        for (CartItem item : items) {
            if (item.getMenuItem().getId().equals(itemId)) {
                return item.getQuantity();
            }
        }
        return 0; // Not in cart
    }
}
