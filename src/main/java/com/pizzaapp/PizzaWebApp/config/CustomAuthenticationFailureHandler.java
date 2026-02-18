package com.pizzaapp.PizzaWebApp.config;

import com.pizzaapp.PizzaWebApp.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // ✅ FIX: Change "username" to "email" to match your HTML form and SecurityConfig
        String email = request.getParameter("email");

        // Safety check: If for some reason email is still null, treat it as a generic error
        if (email == null) {
            setDefaultFailureUrl("/login?error=true");
            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        // Check if this email exists in our DB
        boolean userExists = userRepository.existsById(email);

        if (userExists) {
            // User exists, so the Password must be wrong
            setDefaultFailureUrl("/login?error=password");
        } else {
            // User does not exist (Wrong Email)
            setDefaultFailureUrl("/login?error=email");
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}