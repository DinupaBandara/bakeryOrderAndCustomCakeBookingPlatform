package com.bakenest.Controller;

import com.bakenest.Model.Customer;
import com.bakenest.Service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // 1. Update the existing submit endpoint
    @PostMapping("/customer/feedback/submit")
    public String submitFeedback(@RequestParam Long orderId, @RequestParam int rating,
                                 @RequestParam String description, HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Customer customer = (Customer) session.getAttribute("loggedUser");
        if (customer == null) return "redirect:/login";

        boolean success = feedbackService.saveOrUpdateFeedback(orderId, customer, rating, description);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Your feedback has been saved successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not process feedback.");
        }
        return "redirect:/customer/orders";
    }

    // 2. Add the NEW Delete endpoint
    @PostMapping("/customer/feedback/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Customer customer = (Customer) session.getAttribute("loggedUser");
        if (customer == null) return "redirect:/login";

        boolean success = feedbackService.deleteFeedback(id, customer);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Review deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not delete review.");
        }
        return "redirect:/customer/orders";
    }
}