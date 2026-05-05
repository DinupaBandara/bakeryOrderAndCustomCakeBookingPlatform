# 🥐 BakeNest - Bakery Items & Custom Cake Order Management System

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.x-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?logo=mysql)
![Maven](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven)
![License](https://img.shields.io/badge/License-Proprietary-red)
![Status](https://img.shields.io/badge/Status-Finished-brightgreen)
![Build](https://img.shields.io/badge/Build-Passing-blue)


BakeNest is a full-stack Spring Boot web application designed for a modern bakery. It provides a seamless shopping experience for customers to browse products, customize cakes, and place orders, while offering bakery owners a powerful admin dashboard to manage orders, customer feedback, and dynamic store fees.

## ✨ Features

### Customer Features
* **Product Catalog:** Browse delicious bakery items and pastries.
* **Custom Cake Builder:** Order custom cakes with specific flavors, weights, and designs.
* **Shopping Cart:** Add, remove, and update quantities of items before checkout.
* **Checkout System:** Secure order placement with dynamic delivery and packaging fee calculations.
* **Order Tracking:** View past orders and current order statuses directly from the customer profile.

### Admin Features
* **Dynamic Dashboard:** Real-time statistics including total revenue, order counts, and recent order previews.
* **Order Management:** View detailed, formatted receipts for incoming orders and update statuses (Pending, Preparing, Shipped, Delivered).
* **Global Fee Control:** Instantly adjust store-wide Delivery and Packaging fees that apply to all future checkouts.
* **Customer Feedback:** A dedicated dashboard to track average bakery ratings and read verified customer reviews.

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3.x, Spring MVC
* **Database:** MySQL, Spring Data JPA / Hibernate
* **Frontend:** Thymeleaf, HTML5, CSS3, Vanilla JavaScript
* **Icons & Fonts:** Google Material Symbols, Plus Jakarta Sans
* **Build Tool:** Maven

## 🚀 Getting Started

### Prerequisites
Make sure you have the following installed on your machine:
* [Java Development Kit (JDK) 17 or higher](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Maven](https://maven.apache.org/download.cgi)
* [MySQL Server](https://dev.mysql.com/downloads/mysql/)
* An IDE like IntelliJ IDEA, Eclipse, or VS Code

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/yourusername/bakenest.git](https://github.com/yourusername/bakenest.git)
   cd bakenest


2. **Configure the Database:**
    * Open your MySQL client (e.g., MySQL Workbench) and create a new database:
      ```sql
      CREATE DATABASE bakenest;
      ```
    * Open the `src/main/resources/application.properties` file and update it with your local MySQL credentials:
      ```properties
      # Database Connection
      spring.datasource.url=jdbc:mysql://localhost:3306/bakenest?createDatabaseIfNotExist=true
      spring.datasource.username=root
      spring.datasource.password=YOUR_MYSQL_PASSWORD
      
      # Hibernate Settings
      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.show-sql=true
      ```

3. **Run the Application:**
    * Open your terminal in the project root folder and run:
      ```bash
      mvn spring-boot:run
      ```
    * Alternatively, run the `BakenestApplication.java` file directly from your IDE.

4. **Access the Application:**
    * The application will start on port 8080.
    * Open your web browser and navigate to: `http://localhost:8080`

---

## 📁 Project Structure

BakeNest follows a clean MVC (Model-View-Controller) architecture:

```text
bakenest/
├── src/main/java/com/bakenest/
│   ├── Controller/      # Handles HTTP requests for Admin and Customer routes
│   ├── Model/           # JPA Entities (Order, Customer, Product, Feedback)
│   ├── Repository/      # Spring Data JPA Interfaces for database operations
│   └── Service/         # Core business logic (e.g., OrderService, CartService)
├── src/main/resources/
│   ├── static/          # Static assets (modular CSS files, JavaScript, Images)
│   ├── templates/       # Thymeleaf HTML views (divided by /admin and /customer)
│   └── application.properties # Main configuration file
└── pom.xml              # Maven dependencies
```

---

## 🔮 Future Enhancements
* Integration with a payment gateway (e.g., Stripe or PayPal).
* Implementation of Spring Security for robust role-based authentication and password hashing.
* Image upload capabilities for custom cake design references.
* Email notifications for customers when their order status changes.

---

## 📝 License

**Copyright © 2026 BakeNest. All Rights Reserved.**

This repository and its contents are proprietary and confidential. No part of this software, including but not limited to source code, compiled binaries, and design assets, may be reproduced, distributed, modified, or transmitted in any form or by any means without the prior written permission of the copyright owner.

This is **not** an open-source project. Unauthorized copying of this project, via any medium, is strictly prohibited.