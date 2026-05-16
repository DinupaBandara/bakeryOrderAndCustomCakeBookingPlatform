package com.bakenest.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private double subtotal;

    // --- NEW CHECKOUT FIELDS ---
    private String deliveryAddress;
    private String city;
    private String zipCode;
    private String paymentMethod;
    // ---------------------------

    // Default Fees for BakeNest
    private double deliveryFee;
    private double packagingFee;

    private double totalAmount;

    // Status can be: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    private String status = "PENDING";

    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Feedback feedback;

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
        // Automatically calculate total including fees before saving
        this.totalAmount = this.subtotal + this.deliveryFee + this.packagingFee;
    }
}