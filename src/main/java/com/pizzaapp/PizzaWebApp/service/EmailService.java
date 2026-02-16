package com.pizzaapp.PizzaWebApp.service;

import com.pizzaapp.PizzaWebApp.entity.Order;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import  java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(String to, String name, String orderId, double amount, List<String> items) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("🍕 Order Confirmed! - Sangamesh's Pizza Corner");

            // Build a nice table for the items
            StringBuilder itemsHtml = new StringBuilder();
            for (String item : items) {
                itemsHtml.append("<tr>")
                        .append("<td style='padding: 10px; border-bottom: 1px solid #eee; color: #555;'>").append(item).append("</td>")
                        .append("</tr>");
            }

            String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<div style='background-color: #ff6f61; padding: 20px; text-align: center; color: white;'>" +
                    "<h1>Order Receipt</h1>" +
                    "</div>" +
                    "<div style='padding: 20px; border: 1px solid #eee;'>" +
                    "<p>Hi <strong>" + name + "</strong>, here is your order summary:</p>" +
                    "<table style='width: 100%; border-collapse: collapse;'>" +
                    "<thead><tr style='background-color: #f8f8f8;'><th style='text-align: left; padding: 10px;'>Items & Prices</th></tr></thead>" +
                    "<tbody>" + itemsHtml.toString() + "</tbody>" +
                    "</table>" +
                    "<div style='margin-top: 20px; padding: 10px; background: #fff9f9; border-radius: 5px;'>" +
                    "<p><strong>Order ID:</strong> #" + orderId + "</p>" +
                    "<p style='font-size: 1.2rem;'><strong>Grand Total: </strong> <span style='color: #28a745;'>₹" + amount + "</span></p>" +
                    "</div>" +
                    "<br><a href='http://localhost:8080/orders' style='background-color: #ff6f61; color: white; padding: 12px 25px; text-decoration: none; border-radius: 30px; display: inline-block;'>Track Status</a>" +
                    "</div></body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    // Inside EmailService.java
    public void sendStatusUpdate(String toEmail, String orderId, String newStatus) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("🍕 Order Update: #" + orderId);

            String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<div style='background-color: #343a40; padding: 20px; text-align: center; color: white;'>" +
                    "<h2>Your Pizza is on the Move!</h2>" +
                    "</div>" +
                    "<div style='padding: 20px; border: 1px solid #eee;'>" +
                    "<p>Hello!</p>" +
                    "<p>Your order <strong>#" + orderId + "</strong> status has been updated to:</p>" +
                    "<div style='background-color: #f8f9fa; padding: 15px; text-align: center; font-size: 1.2rem; border: 1px solid #ff6f61; border-radius: 5px;'>" +
                    "<strong>" + newStatus + "</strong>" +
                    "</div>" +
                    "<p>You can check the details anytime on your dashboard.</p>" +
                    "<a href='http://localhost:59227/orders' style='color: #ff6f61; font-weight: bold;'>Go to My Orders</a>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log the error for your internship debugging
            System.err.println("Error sending status update: " + e.getMessage());
        }
    }

    public void sendFeedbackToAdmin(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("admin@gmail.com"); // Sends to YOU (the admin)
            helper.setSubject("⭐ New Review for Order #" + order.getId());

            String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<div style='background-color: #ffc107; padding: 20px; text-align: center; color: #333;'>" +
                    "<h2>New Customer Feedback!</h2>" +
                    "</div>" +
                    "<div style='padding: 20px; border: 1px solid #eee;'>" +
                    "<p><strong>Customer Email:</strong> " + order.getEmail() + "</p>" +
                    "<p><strong>Order ID:</strong> #" + order.getId() + "</p>" +
                    "<hr>" +
                    "<h3>Rating: <span style='color: #ff6f61; font-size: 1.5rem;'>" + order.getRating() + "/5 Stars</span></h3>" +
                    "<p><strong>Feedback:</strong><br><em>\"" + order.getFeedback() + "\"</em></p>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
