# 🍕 Sangamesh's Pizza Corner

> **A Premium Full-Stack E-Commerce Application built with Spring Boot & MongoDB.**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green)](https://www.mongodb.com/atlas)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Sangamesh's Pizza Corner** is a robust web application simulating a real-world pizza ordering system. It bridges the gap between a high-traffic customer storefront and a secure administrative backend, featuring dynamic pricing, real-time order tracking, and inventory management.

---

## 📸 Project Screenshots

|             Customer Home Page              |                 Admin Dashboard                 |
|:-------------------------------------------:|:-----------------------------------------------:|
| ![Home Page](screenshots\customer_home.jpg) | ![Admin Dashboard](\screenshots\admin_home.jpg) |

---

## 🚀 Key Features

### 👤 Customer Experience
* **Smart Menu & Dynamic Pricing:**
    * Browse pizzas with intelligent size calculations (Standard, Medium +₹200, Large +₹400).
    * Search and filter by category (Veg, Non-Veg) or price.
    * **Favorites System:** Save items to a wishlist (AJAX-powered).
* **Live Cart Management:**
    * Real-time quantity updates and total calculations.
    * Coupon system (e.g., apply `WELCOME10` for discounts).
    * Smart validation (checks if the kitchen is open before checkout).
* **Secure Checkout:**
    * Integrated payment simulation (UPI/Card).
    * Digital receipt generation after purchase.
    * **Order Tracking:** Monitor status from "Placed" to "Out for Delivery".

### 🛡️ Admin Dashboard
* **Business Intelligence:** View total revenue, active orders, and customer stats.
* **Menu Management (CRUD):** Add, edit, or delete items, prices, and images.
* **Order Fulfillment:** Update order statuses (Paid → Cooking → Delivered).
* **Store Control:** A global **"Master Switch"** to close the kitchen (disables ordering for all users).

---

## 🛠 Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.x (Web, Data MongoDB, Security, Mail) |
| **Database** | MongoDB Atlas (Cloud NoSQL) |
| **Frontend** | Thymeleaf, HTML5, CSS3 (Glassmorphism UI), JavaScript |
| **Security** | Spring Security (CSRF Protection, Role-Based Access) |
| **Build Tool** | Maven |

---

## ⚙️ Installation & Setup

Follow these steps to run the project locally.

### 1. Clone the Repository
```bash
git clone [https://github.com/sangamesh2k4/pizza-webapp.git](https://github.com/YOUR_USERNAME/pizza-webapp.git)
cd pizza-webapp