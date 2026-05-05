package com.bakenest.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bakenest.Model.CustomCake;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Standard product (NULL if it's a custom cake)
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    // Custom cake (NULL if it's a standard product)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "custom_cake_id", nullable = true)
    private CustomCake customCake;

    private int quantity;

    private double priceAtOrder;

    public double getItemTotal() {
        return this.priceAtOrder * this.quantity;
    }
}