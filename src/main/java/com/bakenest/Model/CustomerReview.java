package com.bakenest.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "customer_reviews")
@Data
@NoArgsConstructor
public class CustomerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false, length = 80)
    private String customerName;

    @Column(name = "favorite_item", length = 100)
    private String favoriteItem;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "review_comment", nullable = false, length = 1000)
    private String comment;

    @Column(name = "owner_token", length = 36)
    private String ownerToken;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // JPA calls this before inserting a new review, so every review gets a created date.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // This helper keeps the Thymeleaf page simple when it needs an avatar letter(kasun-k)
    public String getAvatarLetter() {
        if (customerName == null || customerName.isBlank()) {
            return "?";
        }
        return customerName.trim().substring(0, 1).toUpperCase();
    }

    // Formats the created date for the UI without adding formatting logic to the HTML page.
    public String getDisplayDate() {
        if (createdAt == null) {
            return "Just now";
        }
        return createdAt.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    }

    // Used by the page to show Edit/Delete only to the browser that created the review.
    public boolean isOwnedBy(String currentOwnerToken) {
        return ownerToken != null
                && currentOwnerToken != null
                && ownerToken.equals(currentOwnerToken);
    }
}
