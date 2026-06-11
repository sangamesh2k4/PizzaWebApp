/*package com.pizzaapp.PizzaWebApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.pizzaapp.PizzaWebApp.entity.User;
import com.pizzaapp.PizzaWebApp.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;


    // Handle Register Form Submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if(userService.isEmailTaken(user.getEmail())){
            model.addAttribute("error","Customer with same email ID already exists");
            model.addAttribute("suggestion","try to sign up with different email or login with your existing ID");
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/login"; // redirect to login after registration
    }
}
*/
package com.pizzaapp.PizzaWebApp.controller;

import com.pizzaapp.PizzaWebApp.dto.*;
import com.pizzaapp.PizzaWebApp.entity.RefreshToken;
import com.pizzaapp.PizzaWebApp.entity.User;
import com.pizzaapp.PizzaWebApp.repository.RefreshTokenRepository;
import com.pizzaapp.PizzaWebApp.service.CustomUserDetailsService;
import com.pizzaapp.PizzaWebApp.service.JwtService;

import com.pizzaapp.PizzaWebApp.service.RefreshTokenService;
import com.pizzaapp.PizzaWebApp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final UserService userService;

    private final CustomUserDetailsService
            customUserDetailsService;
    private final RefreshTokenService refreshTokenService;

    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/refresh")
    public RefreshTokenResponse refreshToken(@RequestBody RefreshTokenRequest request){
        RefreshToken storedToken=refreshTokenService.verifyRefreshToken(request.refreshToken());
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
        UserDetails userDetails=customUserDetailsService.loadUserByUsername(storedToken.getUserEmail());
        String accessToken=jwtService.generateAccessToken(userDetails);
        String refreshToken=jwtService.generateRefreshToken(userDetails);
        refreshTokenService.saveRefreshToken(refreshToken,userDetails.getUsername());

        return new RefreshTokenResponse(accessToken,refreshToken);

    }
    @Operation(summary = "Login user and generate JWT tokens")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest request , HttpServletResponse response){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails =
                customUserDetailsService
                        .loadUserByUsername(
                                request.email()
                        );

        String accessToken =
                jwtService.generateAccessToken(
                        userDetails
                );
        String refreshToken=jwtService.generateRefreshToken(userDetails);
        Cookie accessCookie=new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15*60);

        response.addCookie(accessCookie);

        Cookie refreshCookie=new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(30*24*60*60);

        response.addCookie(refreshCookie);
        refreshTokenService.saveRefreshToken(refreshToken,userDetails.getUsername());

        boolean isAdmin =
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority().equals("ADMIN"));

        return ResponseEntity.ok(
                isAdmin
                        ? "/admin/dashboard"
                        : "/home"
        );
    }

    //logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response, HttpServletRequest request){
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if(cookies != null){

            for(Cookie cookie : cookies){

                if("refreshToken".equals(cookie.getName())){
                    refreshToken = cookie.getValue();
                }
            }
        }

        if(refreshToken == null){
            return ResponseEntity.badRequest()
                    .body("Refresh token not found");
        }
        RefreshToken token =
                refreshTokenRepository
                        .findByToken(refreshToken)
                        .orElseThrow(
                                () -> new RuntimeException("token not found")
                        );
        token.setRevoked(true);
        refreshTokenRepository.save(token);
        Cookie accessCookie =
                new Cookie("accessToken", null);

        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        response.addCookie(accessCookie);

        Cookie refreshCookieCookie =
                new Cookie("refreshToken", null);

        refreshCookieCookie.setHttpOnly(true);
        refreshCookieCookie.setPath("/");
        refreshCookieCookie.setMaxAge(0);

        response.addCookie(refreshCookieCookie);
        return ResponseEntity.ok("logged out successfully");
    }
    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {

        if(userService.isEmailTaken(user.getEmail())){


            model.addAttribute("error","Customer with same email ID already exists");
            model.addAttribute("suggestion","try to sign up with different email or login with your existing ID");
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/login"; // redirect to login after registration
    }
}