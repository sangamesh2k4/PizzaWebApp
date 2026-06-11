package com.pizzaapp.PizzaWebApp.service;

import com.pizzaapp.PizzaWebApp.entity.CartEntity;
import com.pizzaapp.PizzaWebApp.model.Cart;
import com.pizzaapp.PizzaWebApp.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    /*public Cart getCart(String email){

        return cartRepository
                .findByUserEmail(email)
                .map(CartEntity::getCart)
                .orElse(new Cart());
    }*/
    public Cart getCart(String email){

        Cart cart = cartRepository
                .findByUserEmail(email)
                .map(CartEntity::getCart)
                .orElse(new Cart());


        return cart;
    }

    public void saveCart(
            String email,
            Cart cart
    ){

        CartEntity entity =
                cartRepository
                        .findByUserEmail(email)
                        .orElse(new CartEntity());

        entity.setUserEmail(email);
        entity.setCart(cart);


        cartRepository.save(entity);
    }
}
