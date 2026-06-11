package com.pizzaapp.PizzaWebApp.config;

import com.pizzaapp.PizzaWebApp.entrypoint.JwtAuthenticationEntryPoint;
import com.pizzaapp.PizzaWebApp.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.pizzaapp.PizzaWebApp.service.CustomUserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomAuthenticationFailureHandler customFailureHandler; //

    @Autowired
    private CustomSuccessHandler customSuccessHandler;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    //private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired  private CustomAccessDeniedHandler accessDeniedHandler;

   
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**"
                        ,"/login"
                        ,"/register"
                        , "/register2"
                        , "/css/**"
                        , "/js/**"
                        , "/images/**"
                        ,"/favicon.ico"
                        ,"/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasAuthority("ADMIN")
                .anyRequest()
                    .authenticated()
            )
            //.formLogin(form -> form
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //.loginPage("/login")
                    //.successHandler(customSuccessHandler)// your custom login page
    //.loginProcessingUrl("/login")     // form POST action
    //.usernameParameter("email")       // ✅ MUST match your input name
    //.passwordParameter("password")
    //.defaultSuccessUrl("/home", true)
    //.failureUrl("/login?error=true")
                   // .failureHandler(customFailureHandler)
//    .permitAll()
//
//)
//
//            .logout(logout -> logout
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/login?logout=true")
//                .permitAll()
//            )
            .csrf(csrf -> csrf.disable());

        return http.build();

    }

 @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception{
        return  configuration.getAuthenticationManager();
    }

    // Password encryption for your UserService
   
}
