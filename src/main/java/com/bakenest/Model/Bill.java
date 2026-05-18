package com.bakenest.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    @Column(length = 1000)
    private String orderItems; 

    private double subtotal;
    private double discount;
    private double finalAmount;

    private String paymentMethod;
    private String paymentStatus; 

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @PrePersist
    @PreUpdate
    public void calculateFinalAmount() {
        this.finalAmount = subtotal - (subtotal * discount / 100);
    }
}
