package com.pizzaapp.PizzaWebApp.repository;

import com.pizzaapp.PizzaWebApp.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query; // 🟢 Import this!

public interface MenuItemRepository extends MongoRepository<MenuItem, String> {
    Page<MenuItem> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);
    Page<MenuItem> findByCategory(String category, Pageable pageable);

    Page<MenuItem> findByAvailable(boolean available, Pageable pageable);
    Page<MenuItem> findByCategoryAndAvailable(String category, boolean available, Pageable pageable);

    @Query("{ 'available': true, '$or': [ {'name': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}} ] }")
    Page<MenuItem> searchActivePizzas(String keyword, Pageable pageable);


}