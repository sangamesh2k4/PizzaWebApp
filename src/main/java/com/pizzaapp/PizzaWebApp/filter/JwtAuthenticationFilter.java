package com.pizzaapp.PizzaWebApp.filter;

import com.pizzaapp.PizzaWebApp.entity.RefreshToken;
import com.pizzaapp.PizzaWebApp.service.CustomUserDetailsService;
import com.pizzaapp.PizzaWebApp.service.JwtService;
import com.pizzaapp.PizzaWebApp.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.refreshTokenService=refreshTokenService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException{
        String jwt = null;
        String username = null;
        String refreshToken=null;

        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for(Cookie cookie : cookies){
                if("accessToken".equals(cookie.getName())){
                    jwt = cookie.getValue();
                }
                if("refreshToken".equals(cookie.getName())){
                    refreshToken=cookie.getValue();
                }
            }
        }

        if(jwt == null && refreshToken != null){

            try{

                RefreshToken storedToken =
                        refreshTokenService.verifyRefreshToken(refreshToken);

                UserDetails userDetails =
                        customUserDetailsService.loadUserByUsername(
                                storedToken.getUserEmail()
                        );

                String newAccessToken =
                        jwtService.generateAccessToken(userDetails);

                Cookie accessCookie =
                        new Cookie("accessToken", newAccessToken);

                accessCookie.setHttpOnly(true);
                accessCookie.setPath("/");
                accessCookie.setMaxAge(15 * 60);

                response.addCookie(accessCookie);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);

            } catch(Exception ex){
                ex.printStackTrace();
            }

            filterChain.doFilter(request,response);
            return;
        }

        //extract username
        try{
            username=jwtService.extractUsername(jwt);
        } catch (Exception e){
            if(refreshToken!=null){
                try{

                    RefreshToken storedToken =
                            refreshTokenService
                                    .verifyRefreshToken(refreshToken);

                    UserDetails userDetails =
                            customUserDetailsService
                                    .loadUserByUsername(
                                            storedToken.getUserEmail()
                                    );

                    String newAccessToken =
                            jwtService.generateAccessToken(
                                    userDetails
                            );

                    jwt = newAccessToken;


                    Cookie accessCookie =
                            new Cookie(
                                    "accessToken",
                                    newAccessToken
                            );

                    accessCookie.setHttpOnly(true);
                    accessCookie.setPath("/");
                    accessCookie.setMaxAge(15 * 60);

                    response.addCookie(accessCookie);

                    username = userDetails.getUsername();


                } catch(Exception ex){

                }
            }
        }

        // Authenticate only if user not already authenticated
        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails=customUserDetailsService.loadUserByUsername(username);

        //validate token
        if(jwtService.isTokenValid(jwt,userDetails)){
            UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource()
                    .buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }}
        filterChain.doFilter(request,response);

}}

