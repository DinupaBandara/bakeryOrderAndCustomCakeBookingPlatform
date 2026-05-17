package com.bakenest.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "custom_cakes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomCake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String weight; // e.g., "1kg", "2kg", "3kg", "5kg"

    @Column(nullable = false)
    private String eventType; // e.g., "Birthday", "Wedding"

    @Column(nullable = false)
    private String flavorType; // e.g., "Tahitian Vanilla Bean"

    @Column(nullable = false)
    private String cakeType; // e.g., "Butter Cake"

    @Column(length = 500)
    private String inscription; // The "Happy Birthday..." text

    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;


    public void calculateAndSetPrice() {
        // 1. Set the Base Price (Starting point for 1kg)
        double basePrice = 1500.00;
        double weightMultiplier = 1.0;
        double flavorPremium = 0.0;
        double typePremium = 0.0;
        double eventPremium = 0.0;

        // 2. Weight Multipliers (Logic for sizes 1kg to 5kg)
        switch (this.weight) {
            case "2kg" -> weightMultiplier = 1.8; // Small discount for larger size
            case "3kg" -> weightMultiplier = 2.5;
            case "5kg" -> weightMultiplier = 4.0;
            default -> weightMultiplier = 1.0; // Default for 1kg
        }

        // 3. Flavor Type Premiums (Ingredient-based costs)
        if (this.flavorType != null) {
            switch (this.flavorType) {
                case "Tahitian Vanilla Bean" -> flavorPremium = 350.00; // Imported beans
                case "Belgian Dark Chocolate" -> flavorPremium = 450.00;
                case "Red Velvet" -> flavorPremium = 400.00;
                case "Classic Ribbon" -> flavorPremium = 200.00;
                default -> flavorPremium = 0.0; // Standard Vanilla/Butter
            }
        }

        // 4. Cake Type Premiums (Texture/Difficulty)
        if (this.cakeType != null) {
            switch (this.cakeType) {
                case "Gateau" -> typePremium = 500.00; // Requires layering/chilling
                case "Coffee Cake" -> typePremium = 300.00;
                case "Butter Cake" -> typePremium = 0.0; // Standard
                default -> typePremium = 0.0;
            }
        }

        // 5. Event Type Premiums (Decoration/Piping complexity)
        if (this.eventType != null) {
            switch (this.eventType) {
                case "Wedding" -> eventPremium = 1000.00; // Heavy intricate piping
                case "Anniversary" -> eventPremium = 500.00;
                case "Birthday" -> eventPremium = 200.00; // Standard message
                default -> eventPremium = 0.0;
            }
        }

        // 6. Final Calculation Of Custom Cake
        // We multiply the base by weight, then add the specific choice premiums
        this.price = (basePrice * weightMultiplier) + flavorPremium + typePremium + eventPremium;
    }
}