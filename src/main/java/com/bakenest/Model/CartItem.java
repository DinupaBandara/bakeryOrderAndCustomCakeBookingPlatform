package com.bakenest.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true) // Allow NULL here
    private Product product;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "custom_cake_id", nullable = true) // Allow NULL here
    private CustomCake customCake;

    // Helper method to get the total price for this item
    public double getTotalPrice() {
        if (this.product != null) {
            return this.product.getPrice() * this.quantity;
        } else if (this.customCake != null) {
            return this.customCake.getPrice() * this.quantity;
        }
        return 0.0;
    }
}