package com.pizzaapp.PizzaWebApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.pizzaapp.PizzaWebApp.entity.User;
import com.pizzaapp.PizzaWebApp.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // For hashing passwords
    @Autowired
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void saveUser(User user) {
        // Encrypt password before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        if(user.getRole() == null || user.getRole().isEmpty()){
            user.setRole("USER");
        }
        user.setEnabled(true);
    userRepository.save(user);
    }

    @Override
    public User findByEmailUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("user with this email not found"+ email));
    }
}
