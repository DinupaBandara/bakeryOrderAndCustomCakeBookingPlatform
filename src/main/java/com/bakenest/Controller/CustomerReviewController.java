package com.bakenest.Controller;

import com.bakenest.Model.CustomerReview;
import com.bakenest.Service.CustomerReviewService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CustomerReviewController {

    private static final String REVIEW_OWNER_COOKIE = "bakenest_review_owner";
    private static final int REVIEW_OWNER_COOKIE_MAX_AGE = 60 * 60 * 24 * 365;

    private final CustomerReviewService customerReviewService;

    // Shows the review page with a blank form and all saved reviews.
    @GetMapping({"/reviews", "/Reviews"})
    public String showReviewsPage(@CookieValue(value = REVIEW_OWNER_COOKIE, required = false) String ownerToken,
                                  HttpServletResponse response,
                                  Model model) {
        String currentOwnerToken = resolveOwnerToken(ownerToken, response);
        addReviewPageData(model, new CustomerReview(), false, currentOwnerToken);
        return "Reviews";
    }

    // CREATE: Handles the form submission for adding a new customer review.
    @PostMapping("/reviews")
    public String createReview(@ModelAttribute("reviewForm") CustomerReview review,
                               @CookieValue(value = REVIEW_OWNER_COOKIE, required = false) String ownerToken,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {
        String currentOwnerToken = resolveOwnerToken(ownerToken, response);

        try {
            customerReviewService.createReview(review, currentOwnerToken);
            redirectAttributes.addFlashAttribute("successMessage", "Review saved successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/reviews";
    }

    // READ for UPDATE: Loads one existing review into the same page form for editing.
    @GetMapping("/reviews/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               @CookieValue(value = REVIEW_OWNER_COOKIE, required = false) String ownerToken,
                               HttpServletResponse response,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        String currentOwnerToken = resolveOwnerToken(ownerToken, response);

        try {
            CustomerReview review = customerReviewService.getReviewById(id);
            if (!review.isOwnedBy(currentOwnerToken)) {
                redirectAttributes.addFlashAttribute("errorMessage", "You can only edit reviews created from this browser.");
                return "redirect:/reviews";
            }
            addReviewPageData(model, review, true, currentOwnerToken);
            return "Reviews";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reviews";
        }
    }

    // UPDATE: Saves changes made in edit mode.
    @PostMapping("/reviews/update/{id}")
    public String updateReview(@PathVariable Long id,
                               @ModelAttribute("reviewForm") CustomerReview review,
                               @CookieValue(value = REVIEW_OWNER_COOKIE, required = false) String ownerToken,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {
        String currentOwnerToken = resolveOwnerToken(ownerToken, response);

        try {
            customerReviewService.updateReview(id, review, currentOwnerToken);
            redirectAttributes.addFlashAttribute("successMessage", "Review updated successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/reviews";
    }

    // DELETE: Removes a review. A POST route is used so delete is not triggered by a normal link click.
    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id,
                               @CookieValue(value = REVIEW_OWNER_COOKIE, required = false) String ownerToken,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {
        String currentOwnerToken = resolveOwnerToken(ownerToken, response);

        try {
            customerReviewService.deleteReview(id, currentOwnerToken);
            redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/reviews";
    }

    // Shared page model builder used by both create mode and edit mode.
    private void addReviewPageData(Model model, CustomerReview reviewForm, boolean editingMode, String currentOwnerToken) {
        List<CustomerReview> reviews = customerReviewService.getAllReviews();

        model.addAttribute("reviewForm", reviewForm);
        model.addAttribute("reviews", reviews);
        model.addAttribute("editingMode", editingMode);
        model.addAttribute("formAction", editingMode ? "/reviews/update/" + reviewForm.getId() : "/reviews");
        model.addAttribute("currentOwnerToken", currentOwnerToken);
    }

    private String resolveOwnerToken(String ownerToken, HttpServletResponse response) {
        if (ownerToken != null && !ownerToken.isBlank()) {
            return ownerToken;
        }

        String newOwnerToken = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(REVIEW_OWNER_COOKIE, newOwnerToken);

        // HttpOnly keeps the token away from JavaScript; the server only needs it for ownership checks.
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(REVIEW_OWNER_COOKIE_MAX_AGE);
        response.addCookie(cookie);

        return newOwnerToken;
    }
}
