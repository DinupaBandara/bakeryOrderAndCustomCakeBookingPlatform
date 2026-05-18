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

    private double amount;
    private double discount;
    private double finalAmount;

    private String paymentMethod;
    private String status; 

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @PrePersist
    @PreUpdate
    public void calculateFinalAmount() {
        this.finalAmount = amount - (amount * discount / 100);
    }
}
