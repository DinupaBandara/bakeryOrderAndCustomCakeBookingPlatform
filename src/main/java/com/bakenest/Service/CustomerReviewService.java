package com.bakenest.Service;

import com.bakenest.Model.CustomerReview;
import com.bakenest.Repository.CustomerReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerReviewService {

    private final CustomerReviewRepository customerReviewRepository;

    // Returns all reviews in newest-first order for the Reviews page.
    public List<CustomerReview> getAllReviews() {
        return customerReviewRepository.findAllByOrderByCreatedAtDesc();
    }

    // Reads one review for the edit form. If the id is wrong, a clear error is thrown.
    public CustomerReview getReviewById(Long id) {
        return customerReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found."));
    }

    // Create operation: saves the browser owner token with a brand-new customer review.
    public CustomerReview createReview(CustomerReview review, String ownerToken) {
        review.setOwnerToken(cleanOwnerToken(ownerToken));
        cleanAndValidate(review);
        return customerReviewRepository.save(review);
    }

    // Update operation:  keeps the existing row id/created date and changes editable fields only.
    public CustomerReview updateReview(Long id, CustomerReview updatedReview, String ownerToken) {
        CustomerReview existingReview = getReviewById(id);
        validateOwner(existingReview, ownerToken);

        existingReview.setCustomerName(updatedReview.getCustomerName());
        existingReview.setFavoriteItem(updatedReview.getFavoriteItem());
        existingReview.setRating(updatedReview.getRating());
        existingReview.setComment(updatedReview.getComment());

        cleanAndValidate(existingReview);
        return customerReviewRepository.save(existingReview);
    }

    // Delete operation: removes a review by primary key.
    public void deleteReview(Long id, String ownerToken) {
        CustomerReview existingReview = getReviewById(id);
        validateOwner(existingReview, ownerToken);
        customerReviewRepository.deleteById(id);
    }

    // Keeps controller code clean and prevents empty/invalid data from being saved.
    private void cleanAndValidate(CustomerReview review) {
        review.setCustomerName(cleanRequiredText(review.getCustomerName(), "Customer name is required."));
        review.setComment(cleanRequiredText(review.getComment(), "Review comment is required."));
        review.setFavoriteItem(cleanOptionalText(review.getFavoriteItem()));

        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
    }

    private String cleanRequiredText(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value.trim();
    }

    private String cleanOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String cleanOwnerToken(String ownerToken) {
        if (ownerToken == null || ownerToken.isBlank()) {
            throw new IllegalArgumentException("Review owner cookie is missing. Please refresh the page and try again.");
        }
        return ownerToken.trim();
    }

    private void validateOwner(CustomerReview review, String ownerToken) {
        String cleanedOwnerToken = cleanOwnerToken(ownerToken);

        // Old reviews without an owner token become read-only instead of being editable by everyone.
        if (!review.isOwnedBy(cleanedOwnerToken)) {
            throw new IllegalArgumentException("You can only edit or delete reviews created from this browser.");
        }
    }
}
