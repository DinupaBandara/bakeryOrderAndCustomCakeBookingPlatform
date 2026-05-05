package com.bakenest.Service;

import com.bakenest.Model.Customer;
import com.bakenest.Model.Feedback;
import com.bakenest.Model.Order;
import com.bakenest.Repository.FeedbackRepository;
import com.bakenest.Repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrderRepository orderRepository;


    // Replace your old submitFeedback method with this saveOrUpdate method
    @Transactional
    public boolean saveOrUpdateFeedback(Long orderId, Customer customer, int rating, String description) {
        Order order = orderRepository.findById(orderId).orElse(null);

        // Security check
        if (order != null && order.getCustomer().getId().equals(customer.getId()) && order.getStatus().equals("DELIVERED")) {

            // Check if feedback exists. If yes, update it. If no, create a new one.
            Feedback feedback = order.getFeedback();
            if (feedback == null) {
                feedback = new Feedback();
                feedback.setOrder(order);
                feedback.setCustomer(customer);
            }

            feedback.setRating(rating);
            feedback.setDescription(description);
            // Optionally update the date so it shows when it was last edited
            feedback.setCreatedAt(java.time.LocalDateTime.now());

            feedbackRepository.save(feedback);
            return true;
        }
        return false;
    }

    // Add this NEW method for Deleting
    @Transactional
    public boolean deleteFeedback(Long feedbackId, Customer customer) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElse(null);

        // Security check to ensure they can only delete their OWN feedback
        if (feedback != null && feedback.getCustomer().getId().equals(customer.getId())) {
            feedbackRepository.delete(feedback);
            return true;
        }
        return false;
    }
}