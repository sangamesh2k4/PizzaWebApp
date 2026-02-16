package com.pizzaapp.PizzaWebApp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PizzaWebAppApplication {

	public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(PizzaWebAppApplication.class, args);
        System.out.println("sangu bhai");
        System.out.println("DEBUG: Testing URI Load -> " + System.getProperty("MONGODB_URI"));
    }
}