package com.pizzaapp.PizzaWebApp.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "contact_messages")
public class ContactMessage {

    @Id
    private String id;

    private String name;
    private String email;
    private String subject;
    private String message;
    private int rating; // Default will be 0

    private Date submittedAt = new Date();//
    private Date date = new Date();
        // Auto-set the date

    public void setMessage(String message) {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}